package com.yamamotoyamato.minline;

import net.minecraft.client.MinecraftClient;

public final class ImeInputController {
    private ImeInputController() {
    }

    public static void update() {
        WindowsImeComposition.setEnabled(MinecraftClient.getInstance().currentScreen != null);
    }
}
