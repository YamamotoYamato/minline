package com.yamamotoyamato.minline;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public final class ImeInputController {
    private static final int FOCUSED_TEXT_FIELD_GRACE_TICKS = 5;

    private static boolean enabled;
    private static boolean forceNextUpdate;
    private static int focusedTextFieldTicks;
    private static boolean hadFocusedTextFieldBeforeBlur;
    private static TextFieldWidget lastFocusedTextField;

    private ImeInputController() {
    }

    public static void update() {
        if (!MinecraftClient.getInstance().isWindowFocused()) {
            return;
        }

        setEnabled(shouldEnableIme(MinecraftClient.getInstance().currentScreen), forceNextUpdate);
        forceNextUpdate = false;

        if (focusedTextFieldTicks > 0) {
            focusedTextFieldTicks--;
        }
    }

    public static void textFieldFocused() {
        focusedTextFieldTicks = FOCUSED_TEXT_FIELD_GRACE_TICKS;
        setEnabled(true, true);
    }

    public static void textFieldFocused(TextFieldWidget textField) {
        lastFocusedTextField = textField;
        textFieldFocused();
    }

    public static void windowFocusChanged(boolean focused) {
        if (focused) {
            if (hadFocusedTextFieldBeforeBlur || isLastTextFieldStillFocused()) {
                focusedTextFieldTicks = FOCUSED_TEXT_FIELD_GRACE_TICKS;
                setEnabled(true, true);
            }
            forceNextUpdate = true;
        } else {
            hadFocusedTextFieldBeforeBlur = focusedTextFieldTicks > 0 || hasFocusedTextField(MinecraftClient.getInstance().currentScreen);
            focusedTextFieldTicks = 0;
            if (!hadFocusedTextFieldBeforeBlur) {
                setEnabled(false, true);
            }
        }
    }

    private static boolean isLastTextFieldStillFocused() {
        return lastFocusedTextField != null && lastFocusedTextField.isActive() && lastFocusedTextField.isFocused();
    }

    private static boolean hasFocusedTextField(Screen screen) {
        return screen != null && hasFocusedTextField((ParentElement) screen);
    }

    private static boolean shouldEnableIme(Screen screen) {
        if (screen == null) {
            return false;
        }

        if (screen instanceof HandledScreen<?>) {
            return focusedTextFieldTicks > 0 || hasFocusedTextField((ParentElement) screen);
        }

        return true;
    }

    private static boolean hasFocusedTextField(ParentElement parent) {
        Element focused = parent.getFocused();
        if (focused != null && hasFocusedTextField(focused)) {
            return true;
        }

        for (Element child : parent.children()) {
            if (hasFocusedTextField(child)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasFocusedTextField(Element element) {
        if (element instanceof TextFieldWidget textField) {
            if (textField.isActive() && textField.isFocused()) {
                lastFocusedTextField = textField;
                return true;
            }
            return false;
        }

        if (element instanceof ParentElement parent) {
            return hasFocusedTextField(parent);
        }

        return false;
    }

    private static void setEnabled(boolean nextEnabled, boolean force) {
        if (!force && enabled == nextEnabled) {
            return;
        }

        WindowsImeComposition.setEnabled(nextEnabled);
        enabled = nextEnabled;
    }
}
