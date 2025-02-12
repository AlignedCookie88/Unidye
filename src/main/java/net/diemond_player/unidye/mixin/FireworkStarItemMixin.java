package net.diemond_player.unidye.mixin;

import net.minecraft.item.FireworkStarItem;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkStarItem.class)
public abstract class FireworkStarItemMixin {
    @Inject(method = "getColorText", at = @At(value = "HEAD"), cancellable = true)
    private static void unidye$getColorText(int color, CallbackInfoReturnable<Text> cir) {
        DyeColor dyeColor = DyeColor.byFireworkColor(color);
        if (dyeColor == null) {
            cir.setReturnValue(Text.literal(String.format("#%06X", (0xFFFFFF & color))));
        }
    }
}
