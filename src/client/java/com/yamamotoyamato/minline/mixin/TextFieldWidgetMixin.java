package com.yamamotoyamato.minline.mixin;

import com.yamamotoyamato.minline.ImeInputController;
import com.yamamotoyamato.minline.WindowsImeComposition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {
    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void inlineJa$renderComposition(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TextFieldWidget widget = (TextFieldWidget) (Object) this;
        if (!widget.isActive() || !widget.isFocused() || MinecraftClient.getInstance().currentScreen == null) {
            return;
        }

        ImeInputController.textFieldFocused();

        String composition = WindowsImeComposition.get();
        if (composition.isEmpty()) {
            return;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int x = widget.getCharacterX(widget.getCursor());
        int y = widget.getY() + (widget.getHeight() - textRenderer.fontHeight) / 2 - 1;

        int maxWidth = Math.max(0, widget.getX() + 4 + widget.getInnerWidth() - x);
        String visibleComposition = textRenderer.trimToWidth(composition, maxWidth);
        if (visibleComposition.isEmpty()) {
            return;
        }

        context.drawText(textRenderer, visibleComposition, x, y, 0xFFE6D45A, false);
        int underlineY = y + textRenderer.fontHeight;
        context.fill(x, underlineY, x + textRenderer.getWidth(visibleComposition), underlineY + 1, 0xFFE6D45A);
    }
}
