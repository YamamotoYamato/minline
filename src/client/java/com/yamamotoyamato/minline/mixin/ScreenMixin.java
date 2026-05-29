package com.yamamotoyamato.minline.mixin;

import com.yamamotoyamato.minline.WindowsImeComposition;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "removed", at = @At("TAIL"))
    private void minline$disableImeOnRemoved(CallbackInfo ci) {
        WindowsImeComposition.setEnabled(false);
    }
}
