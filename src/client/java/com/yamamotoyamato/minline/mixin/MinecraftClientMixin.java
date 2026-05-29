package com.yamamotoyamato.minline.mixin;

import com.yamamotoyamato.minline.ImeInputController;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void minline$updateImeState(CallbackInfo ci) {
        ImeInputController.update();
    }
}
