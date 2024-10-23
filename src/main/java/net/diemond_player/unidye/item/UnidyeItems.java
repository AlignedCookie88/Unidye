package net.diemond_player.unidye.item;

import net.diemond_player.unidye.Unidye;
import net.diemond_player.unidye.block.UnidyeBlocks;
import net.diemond_player.unidye.item.custom.CustomDyeItem;
import net.diemond_player.unidye.item.custom.DyeableBannerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class UnidyeItems {

    public static final Item CUSTOM_DYE = registerItem("custom_dye",
            new CustomDyeItem(new FabricItemSettings()));

    public static final Item CUSTOM_BANNER = registerItem("custom_banner",
            new DyeableBannerItem(UnidyeBlocks.CUSTOM_BANNER, UnidyeBlocks.CUSTOM_WALL_BANNER, new FabricItemSettings().maxCount(16)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Unidye.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Unidye.LOGGER.info("Registering Mod Items for" + Unidye.MOD_ID);
    }

}
