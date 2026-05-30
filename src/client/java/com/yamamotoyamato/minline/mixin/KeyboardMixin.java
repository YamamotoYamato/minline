package com.yamamotoyamato.minline.mixin;

import com.yamamotoyamato.minline.WindowsImeComposition;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void minline$closeImeOnEscape(long window, int action, KeyInput input, CallbackInfo ci) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            WindowsImeComposition.closeIme();
        }
    }
}
