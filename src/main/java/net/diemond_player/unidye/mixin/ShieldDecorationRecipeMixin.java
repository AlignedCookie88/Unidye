package net.diemond_player.unidye.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.diemond_player.unidye.item.custom.DyeableBannerItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShieldDecorationRecipe.class)
public abstract class ShieldDecorationRecipeMixin {
    @ModifyReturnValue(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", at = @At(value = "RETURN"))
    private boolean matches(boolean original, @Local(argsOnly = true) RecipeInputInventory recipeInputInventory, @Local(argsOnly = true) World world) {
        if(original) {
            return doesItActuallyMatchThough(recipeInputInventory, world);
        }
        return false;
    }
    @Unique
    private boolean doesItActuallyMatchThough(RecipeInputInventory recipeInputInventory, World world){
        for (int i = 0; i < recipeInputInventory.size(); ++i) {
            ItemStack itemStack3 = recipeInputInventory.getStack(i);
            if (itemStack3.isEmpty()) continue;
            Item item = itemStack3.getItem();
            if (item instanceof DyeableBannerItem) {
                return false;
            }
        }
        return true;
    }
}
