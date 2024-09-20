package net.diemond_player.unidye.block.custom;

import net.diemond_player.unidye.block.entity.DyeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class DyeableCandleBlock extends CandleBlock implements IDyeableBlock {
    public static final String COLOR_KEY = "color";
    public static final String DISPLAY_KEY = "display";
    public static final int DEFAULT_COLOR = 10511680;
    public DyeableCandleBlock(Settings settings) {
        super(settings);
    }

    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(COLOR_KEY, NbtElement.NUMBER_TYPE)) {
            return nbtCompound.getInt(COLOR_KEY);
        }
        return DEFAULT_COLOR;
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if(getColor(context.getStack())
                != getColor(getPickStack(context.getWorld(), context.getBlockPos(), state))){
            return false;
        }
        return super.canReplace(state, context);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (DyeableBlockEntity.getColor(world, pos) != DyeableBlockEntity.DEFAULT_COLOR) {
            ItemStack stack = super.getPickStack(world, pos, state);
            return pickBlock(world,pos,stack);
        } else {
            return new ItemStack(this);
        }
    }
}
