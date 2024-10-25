package net.diemond_player.unidye.mixin;

import com.google.common.collect.Lists;
import net.diemond_player.unidye.item.UnidyeItems;
import net.diemond_player.unidye.item.custom.CustomDyeItem;
import net.diemond_player.unidye.util.UnidyeUtils;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;

@Mixin(ArmorDyeRecipe.class)
public abstract class ArmorDyeRecipeMixin {
    /**
     * @author Diemond_Player
     * @reason allows for Unidye logic
     */
    @Overwrite
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        ItemStack itemStack = ItemStack.EMPTY;
        ArrayList<ItemStack> list = Lists.newArrayList();
        ArrayList<ItemStack> customColors = Lists.newArrayList();
        for (int i = 0; i < recipeInputInventory.size(); ++i) {
            ItemStack itemStack2 = recipeInputInventory.getStack(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() instanceof DyeableItem) {
                if (itemStack.isEmpty()) {
                    itemStack = itemStack2;
                    continue;
                }
                if (itemStack.getItem() == UnidyeItems.CUSTOM_DYE && itemStack2.getItem() != UnidyeItems.CUSTOM_DYE) {
                    customColors.add(itemStack);
                    itemStack = itemStack2;
                    continue;
                } else if (itemStack.getItem() != UnidyeItems.CUSTOM_DYE && itemStack2.getItem() != UnidyeItems.CUSTOM_DYE) {
                    return false;
                }
            }
            if (itemStack2.getItem() == UnidyeItems.CUSTOM_DYE) {
                customColors.add(itemStack2);
                continue;
            }
            if (itemStack2.getItem() instanceof DyeItem) {
                list.add(itemStack2);
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && (!list.isEmpty() || !customColors.isEmpty());
    }

    /**
     * @author Diemond_Player
     * @reason allows for Unidye logic
     */
    @Overwrite
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ArrayList<DyeItem> list = Lists.newArrayList();
        ArrayList<ItemStack> customColors = Lists.newArrayList();
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < recipeInputInventory.size(); ++i) {
            ItemStack itemStack2 = recipeInputInventory.getStack(i);
            if (itemStack2.isEmpty()) continue;
            Item item = itemStack2.getItem();
            if (item instanceof DyeableItem) {
                if (itemStack.isEmpty()) {
                    itemStack = itemStack2;
                    continue;
                }
                if (itemStack.getItem() instanceof CustomDyeItem && !(item instanceof CustomDyeItem)) {
                    customColors.add(itemStack);
                    itemStack = itemStack2;
                    continue;
                } else if (!(itemStack.getItem() instanceof CustomDyeItem) && !(item instanceof CustomDyeItem)) {
                    return ItemStack.EMPTY;
                }
            }
            if (item instanceof CustomDyeItem) {
                customColors.add(itemStack2);
                continue;
            }
            if (item instanceof DyeItem) {
                list.add((DyeItem) item);
                continue;
            }
            return ItemStack.EMPTY;
        }
        ItemStack itemStack1 = UnidyeUtils.blendAndSetColor(itemStack, list, customColors);
        if (itemStack1.isOf(UnidyeItems.CUSTOM_DYE)) {
            itemStack1.setCount(list.size() + customColors.size() + 1);
        }
        return itemStack1;
    }
}