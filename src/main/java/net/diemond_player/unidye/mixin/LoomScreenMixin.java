package net.diemond_player.unidye.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.diemond_player.unidye.block.entity.DyeableBannerBlockEntity;
import net.diemond_player.unidye.entity.client.renderer.DyeableBannerBlockEntityRenderer;
import net.diemond_player.unidye.item.UnidyeItems;
import net.diemond_player.unidye.item.custom.DyeableBannerItem;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LoomScreen.class)
public abstract class LoomScreenMixin {

    @Shadow
    private ItemStack banner;

    @Unique
    private List<Pair<RegistryEntry<BannerPattern>, ?>> bannerPatterns;

    @Shadow
    private ItemStack dye;

    protected LoomScreenMixin(List<Pair<RegistryEntry<BannerPattern>, ?>> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BannerBlockEntityRenderer;renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;)V"))
    private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns) {
        if (banner.isOf(UnidyeItems.CUSTOM_BANNER)) {
            DyeableBannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, canvas, baseSprite, isBanner, bannerPatterns);
        } else if (dye.isOf(UnidyeItems.CUSTOM_DYE)) {
            DyeableBannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, canvas, baseSprite, isBanner, bannerPatterns);
        } else {
            BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, canvas, baseSprite, isBanner, patterns);
        }
    }

    @Inject(method = "onInventoryChanged", at = @At(value = "TAIL"))
    private void onInventoryChanged(CallbackInfo ci, @Local(ordinal = 0) ItemStack itemStack) {
        if (itemStack.isOf(UnidyeItems.CUSTOM_BANNER)) {
            bannerPatterns = DyeableBannerBlockEntity.getPatternsFromNbt(((DyeableBannerItem) itemStack.getItem()).getColor(itemStack), DyeableBannerBlockEntity.getPatternListNbt(itemStack));
        }
    }
}
