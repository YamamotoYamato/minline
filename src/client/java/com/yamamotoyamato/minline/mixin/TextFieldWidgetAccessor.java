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
}
