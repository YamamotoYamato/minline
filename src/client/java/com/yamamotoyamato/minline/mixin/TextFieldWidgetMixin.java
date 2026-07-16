package com.yamamotoyamato.minline.mixin;

import com.yamamotoyamato.minline.WindowsImeComposition;
import com.yamamotoyamato.minline.ImeCandidateOverlay;
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
    private String minline$originalText;

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void minline$hideSuffixForRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TextFieldWidget widget = (TextFieldWidget) (Object) this;
        String composition = WindowsImeComposition.get();
        int cursor = widget.getCursor();
        if (!widget.isActive() || !widget.isFocused() || composition.isEmpty() || cursor >= widget.getText().length()) {
            return;
        }

        TextFieldWidgetAccessor accessor = (TextFieldWidgetAccessor) widget;
        minline$originalText = widget.getText();
        accessor.minline$setText(minline$originalText.substring(0, cursor));
        accessor.minline$setSelectionStart(cursor);
        accessor.minline$setSelectionEnd(cursor);
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void inlineJa$renderComposition(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TextFieldWidget widget = (TextFieldWidget) (Object) this;
        TextFieldWidgetAccessor accessor = (TextFieldWidgetAccessor) widget;
        String renderedText = minline$originalText;
        if (renderedText != null) {
            accessor.minline$setText(renderedText);
            minline$originalText = null;
        }
        if (!widget.isActive() || !widget.isFocused() || MinecraftClient.getInstance().currentScreen == null) {
            return;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int firstCharacterIndex = accessor.minline$getFirstCharacterIndex();
        int cursor = widget.getCursor();
        int x = accessor.minline$getTextX();
        if (cursor > firstCharacterIndex) {
            x += textRenderer.getWidth(widget.getText().substring(firstCharacterIndex, cursor));
        }
        int y = accessor.minline$getTextY();
        WindowsImeComposition.moveCompositionWindow(x, y + textRenderer.fontHeight);

        String composition = WindowsImeComposition.get();
        WindowsImeComposition.Candidates candidates = WindowsImeComposition.getCandidates();
        ImeCandidateOverlay.update(candidates, x, y);
        if (composition.isEmpty() && candidates.isEmpty()) {
            return;
        }

        int maxWidth = Math.max(0, accessor.minline$getTextX() + widget.getInnerWidth() - x);
        String visibleComposition = textRenderer.trimToWidth(composition, maxWidth);
        if (!visibleComposition.isEmpty()) {
            int compositionWidth = textRenderer.getWidth(visibleComposition);
            String suffix = widget.getText().substring(Math.min(cursor, widget.getText().length()));
            String visibleSuffix = textRenderer.trimToWidth(suffix, Math.max(0, maxWidth - compositionWidth));
            context.drawText(textRenderer, visibleComposition, x, y, 0xFFE6D45A, false);
            int underlineY = y + textRenderer.fontHeight;
            context.fill(x, underlineY, x + compositionWidth, underlineY + 1, 0xFFE6D45A);
            if (!visibleSuffix.isEmpty()) {
                context.drawText(textRenderer, visibleSuffix, x + compositionWidth, y, 0xFFFFFFFF, false);
            }
        }

    }

}
