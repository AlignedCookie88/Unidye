package net.diemond_player.unidye.mixin;

import com.mojang.datafixers.util.Pair;
import net.diemond_player.unidye.block.UnidyeBlocks;
import net.diemond_player.unidye.block.custom.DyeableBannerBlock;
import net.diemond_player.unidye.block.custom.DyeableBedBlock;
import net.diemond_player.unidye.block.custom.DyeableShulkerBoxBlock;
import net.diemond_player.unidye.block.custom.DyeableWallBannerBlock;
import net.diemond_player.unidye.block.entity.DyeableBannerBlockEntity;
import net.diemond_player.unidye.block.entity.DyeableBedBlockEntity;
import net.diemond_player.unidye.block.entity.DyeableShulkerBoxBlockEntity;
import net.diemond_player.unidye.entity.client.renderer.DyeableBannerBlockEntityRenderer;
import net.diemond_player.unidye.item.custom.DyeableBannerItem;
import net.diemond_player.unidye.item.custom.DyeableBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
    @Mutable
    @Final
    @Shadow
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    @Unique
    private final DyeableBedBlockEntity renderDyeableBed = new DyeableBedBlockEntity(BlockPos.ORIGIN, UnidyeBlocks.CUSTOM_BED.getDefaultState());
    @Unique
    private final DyeableShulkerBoxBlockEntity RENDER_DYEABLE_SHULKER_BOX = new DyeableShulkerBoxBlockEntity(BlockPos.ORIGIN, UnidyeBlocks.CUSTOM_SHULKER_BOX.getDefaultState());
    @Unique
    private final DyeableBannerBlockEntity renderDyeableBanner = new DyeableBannerBlockEntity(BlockPos.ORIGIN, UnidyeBlocks.CUSTOM_BANNER.getDefaultState());
    @Shadow
    private ShieldEntityModel modelShield;

    protected BuiltinModelItemRendererMixin(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof DyeableBedBlock) {
                this.renderDyeableBed.color = ((DyeableBlockItem) item).getColor(stack);
                this.blockEntityRenderDispatcher.renderEntity(this.renderDyeableBed, matrices, vertexConsumers, light, overlay);
                ci.cancel();
            }
            if (block instanceof DyeableShulkerBoxBlock) {
                this.RENDER_DYEABLE_SHULKER_BOX.color = ((DyeableBlockItem) item).getColor(stack);
                this.blockEntityRenderDispatcher.renderEntity(this.RENDER_DYEABLE_SHULKER_BOX, matrices, vertexConsumers, light, overlay);
                ci.cancel();
            }
            if (block instanceof DyeableBannerBlock || block instanceof DyeableWallBannerBlock) {
                this.renderDyeableBanner.color = ((DyeableBannerItem) item).getColor(stack);
                this.renderDyeableBanner.readFrom(stack);
                this.blockEntityRenderDispatcher.renderEntity(this.renderDyeableBanner, matrices, vertexConsumers, light, overlay);
                ci.cancel();
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BannerBlockEntity;getPatternListNbt(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NbtList;"), cancellable = true)
    private void render1(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (BlockItem.getBlockEntityNbt(stack).contains("CustomColored")) {
            List<Pair<RegistryEntry<BannerPattern>, ?>> list = DyeableBannerBlockEntity.getPatternsFromNbt(BlockItem.getBlockEntityNbt(stack).getInt("Base"), DyeableBannerBlockEntity.getPatternListNbt(stack));
            DyeableBannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, this.modelShield.getPlate(), ModelLoader.SHIELD_BASE, false, list, stack.hasGlint());
            matrices.pop();
            ci.cancel();
        }
    }
}
