package com.yamamotoyamato.minline.mixin;

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

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        TextFieldWidgetAccessor accessor = (TextFieldWidgetAccessor) widget;
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
        if (composition.isEmpty() && candidates.isEmpty()) {
            return;
        }

        int maxWidth = Math.max(0, accessor.minline$getTextX() + widget.getInnerWidth() - x);
        String visibleComposition = textRenderer.trimToWidth(composition, maxWidth);
        if (!visibleComposition.isEmpty()) {
            context.drawText(textRenderer, visibleComposition, x, y, 0xFFE6D45A, false);
            int underlineY = y + textRenderer.fontHeight;
            context.fill(x, underlineY, x + textRenderer.getWidth(visibleComposition), underlineY + 1, 0xFFE6D45A);
        }

        if (!candidates.isEmpty()) {
            inlineJa$renderCandidates(context, textRenderer, candidates, x, y);
        }
    }

    private static void inlineJa$renderCandidates(
            DrawContext context,
            TextRenderer textRenderer,
            WindowsImeComposition.Candidates candidates,
            int x,
            int textY
    ) {
        int pageStart = Math.max(0, candidates.pageStart());
        int pageSize = candidates.pageSize() > 0 ? candidates.pageSize() : 9;
        int end = Math.min(candidates.values().size(), pageStart + Math.min(pageSize, 9));
        if (pageStart >= end) {
            return;
        }

        int width = 0;
        for (int i = pageStart; i < end; i++) {
            width = Math.max(width, textRenderer.getWidth((i - pageStart + 1) + ". " + candidates.values().get(i)));
        }

        int rowHeight = textRenderer.fontHeight + 3;
        int boxWidth = width + 8;
        int boxHeight = (end - pageStart) * rowHeight + 5;
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int belowY = textY + textRenderer.fontHeight + 3;
        int aboveY = textY - boxHeight - 3;
        int y = belowY + boxHeight <= screenHeight ? belowY : Math.max(3, aboveY);
        context.fill(x - 3, y - 3, x - 3 + boxWidth, y - 3 + boxHeight, 0xE0101010);
        context.fill(x - 3, y - 3, x - 3 + boxWidth, y - 2, 0xFFE6D45A);
        context.fill(x - 3, y - 4 + boxHeight, x - 3 + boxWidth, y - 3 + boxHeight, 0xFFE6D45A);
        context.fill(x - 3, y - 3, x - 2, y - 3 + boxHeight, 0xFFE6D45A);
        context.fill(x - 4 + boxWidth, y - 3, x - 3 + boxWidth, y - 3 + boxHeight, 0xFFE6D45A);

        for (int i = pageStart; i < end; i++) {
            int rowY = y + (i - pageStart) * rowHeight;
            if (i == candidates.selection()) {
                context.fill(x - 1, rowY - 1, x - 1 + boxWidth - 4, rowY + rowHeight - 1, 0x55E6D45A);
            }
            context.drawText(textRenderer, (i - pageStart + 1) + ". " + candidates.values().get(i), x + 1, rowY, 0xFFFFFFFF, false);
        }
    }
}
