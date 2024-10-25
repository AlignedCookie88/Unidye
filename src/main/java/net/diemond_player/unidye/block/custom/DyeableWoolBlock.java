package net.diemond_player.unidye.block.custom;

import net.diemond_player.unidye.block.entity.DyeableLeatheryBlockEntity;
import net.diemond_player.unidye.block.entity.UnidyeBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import static net.diemond_player.unidye.block.entity.DyeableLeatheryBlockEntity.DEFAULT_COLOR;

public class DyeableWoolBlock extends DyeableBlock {
    public DyeableWoolBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (DyeableLeatheryBlockEntity.getColor(world, pos) != DEFAULT_COLOR) {
            return pickBlock(world, pos, new ItemStack(this));
        } else {
            return new ItemStack(this);
        }
    }

    @Override
    public ItemStack pickBlock(BlockView world, BlockPos pos, ItemStack stack) {
        DyeableLeatheryBlockEntity blockEntity = UnidyeBlockEntities.DYEABLE_LEATHERY_BE.get(world, pos);
        int color = DEFAULT_COLOR;
        int bannerColor = DEFAULT_COLOR;
        if (blockEntity != null) {
            color = blockEntity.color;
            bannerColor = blockEntity.leatherColor;
        }
        NbtCompound subNbt = stack.getOrCreateSubNbt("display");
        subNbt.putInt("color", color);
        if(stack.getNbt()!=null) {
            stack.getNbt().putInt("leather", bannerColor);
        }
        return stack;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DyeableLeatheryBlockEntity(pos, state);
    }
}
