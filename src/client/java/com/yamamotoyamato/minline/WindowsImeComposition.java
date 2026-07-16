package com.yamamotoyamato.minline;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.Platform;

import java.util.ArrayList;
import java.util.List;

public final class WindowsImeComposition {
    private static final int GCS_COMPSTR = 0x0008;
    private static final int GCS_RESULTSTR = 0x0800;
    private static final int CANDIDATE_LIST_INDEX = 0;
    private static final int CFS_FORCE_POSITION = 0x0020;
    private static final int GWLP_WNDPROC = -4;
    private static final int WM_IME_STARTCOMPOSITION = 0x010D;
    private static final int WM_IME_COMPOSITION = 0x010F;
    private static final int WM_IME_NOTIFY = 0x0282;
    private static final int IMN_CHANGECANDIDATE = 0x0003;
    private static final int IMN_OPENCANDIDATE = 0x0005;
    private static final int VK_IME_OFF = 0x1A;
    private static final int KEYEVENTF_KEYUP = 0x0002;
    private static final long INLINE_INPUT_ACTIVE_NANOS = 500_000_000L;
    private static volatile boolean available;
    private static volatile long inlineInputActiveUntil;
    private static HWND hookedHwnd;
    private static LONG_PTR previousWndProc;
    private static WinUser.WindowProc windowProc;

    private WindowsImeComposition() {
    }

    public static void initialize() {
        available = Platform.get() == Platform.WINDOWS;
    }

    public static void closeIme() {
        if (!available) {
            return;
        }

        HWND hwnd = getWindowHandle();
        if (hwnd == null) {
            return;
        }

        Pointer context = Imm32.INSTANCE.ImmGetContext(hwnd);
        if (context == null || Pointer.nativeValue(context) == 0L) {
            return;
        }

        try {
            Imm32.INSTANCE.ImmSetOpenStatus(context, false);
        } finally {
            Imm32.INSTANCE.ImmReleaseContext(hwnd, context);
        }
        User32.INSTANCE.keybd_event((byte) VK_IME_OFF, (byte) 0, 0, Pointer.NULL);
        User32.INSTANCE.keybd_event((byte) VK_IME_OFF, (byte) 0, KEYEVENTF_KEYUP, Pointer.NULL);
    }

    public static boolean isImeOpen() {
        if (!available) {
            return false;
        }

        HWND hwnd = getWindowHandle();
        if (hwnd == null) {
            return false;
        }

        Pointer context = Imm32.INSTANCE.ImmGetContext(hwnd);
        if (context == null || Pointer.nativeValue(context) == 0L) {
            return false;
        }

        try {
            return Imm32.INSTANCE.ImmGetOpenStatus(context);
        } finally {
            Imm32.INSTANCE.ImmReleaseContext(hwnd, context);
        }
    }

    public static void moveCompositionWindow(int guiX, int guiY) {
        if (!available) {
            return;
        }

        HWND hwnd = getWindowHandle();
        if (hwnd == null) {
            return;
        }
        markInlineInputActive();
        installMessageHook(hwnd);

        Pointer context = Imm32.INSTANCE.ImmGetContext(hwnd);
        if (context == null || Pointer.nativeValue(context) == 0L) {
            return;
        }

        try {
            Window window = MinecraftClient.getInstance().getWindow();
            double scaleFactor = window.getScaleFactor();
            CompositionForm form = new CompositionForm();
            form.dwStyle = CFS_FORCE_POSITION;
            form.ptCurrentPos.x = (int) Math.round(guiX * scaleFactor);
            form.ptCurrentPos.y = (int) Math.round(guiY * scaleFactor);
            form.write();
            Imm32.INSTANCE.ImmSetCompositionWindow(context, form);
        } finally {
            Imm32.INSTANCE.ImmReleaseContext(hwnd, context);
        }
    }

    private static void markInlineInputActive() {
        inlineInputActiveUntil = System.nanoTime() + INLINE_INPUT_ACTIVE_NANOS;
    }

    private static boolean isInlineInputActive() {
        return System.nanoTime() <= inlineInputActiveUntil;
    }

    private static void installMessageHook(HWND hwnd) {
        if (windowProc != null && Pointer.nativeValue(hwnd.getPointer()) == Pointer.nativeValue(hookedHwnd.getPointer())) {
            return;
        }

        windowProc = WindowsImeComposition::handleWindowMessage;
        previousWndProc = User32.INSTANCE.SetWindowLongPtr(hwnd, GWLP_WNDPROC, windowProc);
        hookedHwnd = hwnd;
    }

    private static LRESULT handleWindowMessage(HWND hwnd, int message, WPARAM wParam, LPARAM lParam) {
        if (isInlineInputActive() && message == WM_IME_STARTCOMPOSITION) {
            return new LRESULT(0);
        }

        if (isInlineInputActive() && message == WM_IME_COMPOSITION && (lParam.longValue() & GCS_RESULTSTR) == 0L) {
            return new LRESULT(0);
        }

        if (isInlineInputActive() && message == WM_IME_NOTIFY
                && (wParam.intValue() == IMN_OPENCANDIDATE || wParam.intValue() == IMN_CHANGECANDIDATE)) {
            return new LRESULT(0);
        }

        if (previousWndProc == null || previousWndProc.longValue() == 0L) {
            return User32.INSTANCE.DefWindowProc(hwnd, message, wParam, lParam);
        }

        return User32.INSTANCE.CallWindowProc(previousWndProc, hwnd, message, wParam, lParam);
    }

    public static String get() {
        if (!available) {
            return "";
        }

        HWND hwnd = getWindowHandle();
        if (hwnd == null) {
            return "";
        }

        Pointer context = Imm32.INSTANCE.ImmGetContext(hwnd);
        if (context == null || Pointer.nativeValue(context) == 0L) {
            return "";
        }

        try {
            int byteLength = Imm32.INSTANCE.ImmGetCompositionStringW(context, GCS_COMPSTR, null, 0);
            if (byteLength <= 0) {
                return "";
            }

            char[] buffer = new char[(byteLength / Character.BYTES) + 1];
            int copied = Imm32.INSTANCE.ImmGetCompositionStringW(context, GCS_COMPSTR, buffer, byteLength);
            if (copied <= 0) {
                return "";
            }

            return Native.toString(buffer);
        } finally {
            Imm32.INSTANCE.ImmReleaseContext(hwnd, context);
        }
    }

    public static Candidates getCandidates() {
        if (!available) {
            return Candidates.EMPTY;
        }

        HWND hwnd = getWindowHandle();
        if (hwnd == null) {
            return Candidates.EMPTY;
        }

        Pointer context = Imm32.INSTANCE.ImmGetContext(hwnd);
        if (context == null || Pointer.nativeValue(context) == 0L) {
            return Candidates.EMPTY;
        }

        try {
            int byteLength = Imm32.INSTANCE.ImmGetCandidateListW(context, CANDIDATE_LIST_INDEX, null, 0);
            if (byteLength <= 0) {
                return Candidates.EMPTY;
            }

            Memory memory = new Memory(byteLength);
            int copied = Imm32.INSTANCE.ImmGetCandidateListW(context, CANDIDATE_LIST_INDEX, memory, byteLength);
            if (copied <= 0) {
                return Candidates.EMPTY;
            }

            int count = memory.getInt(8);
            int selection = memory.getInt(12);
            int pageStart = memory.getInt(16);
            int pageSize = memory.getInt(20);
            List<String> values = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                long offset = Integer.toUnsignedLong(memory.getInt(24L + (long) i * Integer.BYTES));
                if (offset > 0 && offset < byteLength) {
                    String value = memory.getWideString(offset);
                    if (!value.isEmpty()) {
                        values.add(value);
                    }
                }
            }

            if (values.isEmpty()) {
                return Candidates.EMPTY;
            }
            return new Candidates(List.copyOf(values), selection, pageStart, pageSize);
        } finally {
            Imm32.INSTANCE.ImmReleaseContext(hwnd, context);
        }
    }

    private static HWND getWindowHandle() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        if (window == 0L) {
            return null;
        }

        long nativeWindow = GLFWNativeWin32.glfwGetWin32Window(window);
        if (nativeWindow == 0L) {
            return null;
        }

        return new HWND(Pointer.createConstant(nativeWindow));
    }

    private interface Imm32 extends StdCallLibrary {
        Imm32 INSTANCE = Native.load("imm32", Imm32.class, W32APIOptions.DEFAULT_OPTIONS);

        Pointer ImmGetContext(WinUser.HWND hwnd);

        boolean ImmReleaseContext(WinUser.HWND hwnd, Pointer inputContext);

        int ImmGetCompositionStringW(Pointer inputContext, int index, char[] buffer, int bufferLength);

        boolean ImmSetCompositionWindow(Pointer inputContext, CompositionForm compositionForm);

        int ImmGetCandidateListW(Pointer inputContext, int candidateListIndex, Pointer candidateList, int bufferLength);

        boolean ImmSetOpenStatus(Pointer inputContext, boolean open);

        boolean ImmGetOpenStatus(Pointer inputContext);
    }

    private interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        LONG_PTR SetWindowLongPtr(HWND hwnd, int index, WinUser.WindowProc wndProc);

        LRESULT CallWindowProc(LONG_PTR previousWndProc, HWND hwnd, int message, WPARAM wParam, LPARAM lParam);

        LRESULT DefWindowProc(HWND hwnd, int message, WPARAM wParam, LPARAM lParam);

        void keybd_event(byte virtualKey, byte scanCode, int flags, Pointer extraInfo);
    }

    public static class CompositionForm extends Structure {
        public int dwStyle;
        public POINT ptCurrentPos = new POINT();
        public RECT rcArea = new RECT();

        @Override
        protected List<String> getFieldOrder() {
            return List.of("dwStyle", "ptCurrentPos", "rcArea");
        }
    }

    public record Candidates(List<String> values, int selection, int pageStart, int pageSize) {
        public static final Candidates EMPTY = new Candidates(List.of(), 0, 0, 0);

        public boolean isEmpty() {
            return values.isEmpty();
        }
    }
}
