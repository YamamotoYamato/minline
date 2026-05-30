package com.yamamotoyamato.minline;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.Platform;

public final class WindowsImeComposition {
    private static final int GCS_COMPSTR = 0x0008;
    private static final int IACE_DEFAULT = 0x0010;
    private static final int IACE_IGNORENOCONTEXT = 0x0020;
    private static volatile boolean available;

    private WindowsImeComposition() {
    }

    public static void initialize() {
        available = Platform.get() == Platform.WINDOWS;
    }

    public static String get() {
        if (!available) {
            return "";
        }

        long window = MinecraftClient.getInstance().getWindow().getHandle();
        if (window == 0L) {
            return "";
        }

        HWND hwnd = new HWND(Pointer.createConstant(GLFWNativeWin32.glfwGetWin32Window(window)));
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

    public static void setEnabled(boolean enabled) {
        if (!available) {
            return;
        }

        HWND hwnd = getWindowHandle();
        if (hwnd == null) {
            return;
        }

        if (enabled) {
            User32.INSTANCE.BringWindowToTop(hwnd);
            User32.INSTANCE.SetForegroundWindow(hwnd);
            User32.INSTANCE.SetActiveWindow(hwnd);
            User32.INSTANCE.SetFocus(hwnd);
            Imm32.INSTANCE.ImmAssociateContextEx(hwnd, null, IACE_DEFAULT);
        } else {
            Imm32.INSTANCE.ImmAssociateContextEx(hwnd, null, IACE_IGNORENOCONTEXT);
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

        boolean ImmAssociateContextEx(WinUser.HWND hwnd, Pointer inputContext, int flags);
    }

    private interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean BringWindowToTop(WinUser.HWND hwnd);

        boolean SetForegroundWindow(WinUser.HWND hwnd);

        WinUser.HWND SetActiveWindow(WinUser.HWND hwnd);

        WinUser.HWND SetFocus(WinUser.HWND hwnd);
    }
}
