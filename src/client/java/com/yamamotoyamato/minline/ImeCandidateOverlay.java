package com.yamamotoyamato.minline;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** IME候補を画面の最前面へ描画する処理を管理するクラス */
public final class ImeCandidateOverlay {
    private static int x;
    private static int textY;
    private static WindowsImeComposition.Candidates candidates = WindowsImeComposition.Candidates.EMPTY;

    private ImeCandidateOverlay() {
    }

    /** 現在のIME候補と描画位置を更新する */
    public static void update(WindowsImeComposition.Candidates value, int candidateX, int candidateY) {
        candidates = value;
        x = candidateX;
        textY = candidateY;
    }

    /** 画面全体の描画後にIME候補を最前面へ描画する */
    public static void render(DrawContext context) {
        if (candidates.isEmpty()) {
            return;
        }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
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
