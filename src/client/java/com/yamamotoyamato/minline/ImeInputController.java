package com.yamamotoyamato.minline;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public final class ImeInputController {
    private static boolean enabled;

    private ImeInputController() {
    }

    public static void update() {
        setEnabled(shouldEnableIme(MinecraftClient.getInstance().currentScreen));
    }

    public static void textFieldFocused() {
        setEnabled(true);
    }

    private static boolean shouldEnableIme(Screen screen) {
        if (screen == null) {
            return false;
        }

        if (screen instanceof HandledScreen<?>) {
            return hasFocusedTextField(screen);
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
            return textField.isActive() && textField.isFocused();
        }

        if (element instanceof ParentElement parent) {
            return hasFocusedTextField(parent);
        }

        return false;
    }

    private static void setEnabled(boolean nextEnabled) {
        if (enabled == nextEnabled) {
            return;
        }

        WindowsImeComposition.setEnabled(nextEnabled);
        enabled = nextEnabled;
    }
}
