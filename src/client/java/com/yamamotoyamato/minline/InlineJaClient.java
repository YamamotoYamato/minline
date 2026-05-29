package com.yamamotoyamato.minline;

import net.fabricmc.api.ClientModInitializer;

public final class InlineJaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WindowsImeComposition.initialize();
    }
}
