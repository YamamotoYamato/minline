package com.yamamotoyamato.minline.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldWidget.class)
public interface TextFieldWidgetAccessor {
    @Accessor("textX")
    int minline$getTextX();

    @Accessor("textY")
    int minline$getTextY();

    @Accessor("firstCharacterIndex")
    int minline$getFirstCharacterIndex();

    /** テキストフィールドの描画用文字列を設定する */
    @Accessor("text")
    void minline$setText(String text);

    /** テキストフィールドの選択開始位置を設定する */
    @Accessor("selectionStart")
    void minline$setSelectionStart(int selectionStart);

    /** テキストフィールドの選択終了位置を設定する */
    @Accessor("selectionEnd")
    void minline$setSelectionEnd(int selectionEnd);
}
