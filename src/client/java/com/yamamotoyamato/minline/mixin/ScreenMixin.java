package com.yamamotoyamato.minline.mixin;

import com.yamamotoyamato.minline.ImeCandidateOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    /** 画面内のウィジェットより後にIME候補を描画する */
    @Inject(method = "renderWithTooltip", at = @At("TAIL"))
    private void minline$renderImeCandidatesOnTop(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ImeCandidateOverlay.render(context);
    }
}
