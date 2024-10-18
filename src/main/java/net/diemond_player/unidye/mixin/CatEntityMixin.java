package net.diemond_player.unidye.mixin;

import net.diemond_player.unidye.item.custom.CustomDyeItem;
import net.diemond_player.unidye.util.IEntityAccessor;
import net.diemond_player.unidye.util.UnidyeUtils;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin implements IEntityAccessor {
    @Unique
    private static final TrackedData<Integer> CUSTOM_COLOR = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("unidye.custom_color", unidye$getCustomColor());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("unidye.custom_color")) {
            unidye$setCustomColor(nbt.getInt("unidye.custom_color"));
        }
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    private void initDataTracker(CallbackInfo ci){
        ((CatEntity) (Object) this).getDataTracker().startTracking(CUSTOM_COLOR, 0xFFFFFF);
    }


    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CatEntity;setCollarColor(Lnet/minecraft/util/DyeColor;)V"))
    private void interactMobReset(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        IEntityAccessor cat = (IEntityAccessor) ((CatEntity) (Object) this);
        cat.unidye$setCustomColor(0xFFFFFF);
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/DyeItem;getColor()Lnet/minecraft/util/DyeColor;", shift = At.Shift.AFTER), cancellable = true)
    private void interactMobCheck(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        IEntityAccessor cat = (IEntityAccessor) ((CatEntity) (Object) this);
        DyeColor dyeColor = ((DyeItem)item).getColor();
        if (cat.unidye$getCustomColor() != 0xFFFFFF) {
            ((CatEntity) (Object) this).setCollarColor(dyeColor);
            cat.unidye$setCustomColor(0xFFFFFF);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            ((CatEntity) (Object) this).setPersistent();
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void interactMobSet(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        IEntityAccessor cat = (IEntityAccessor) ((CatEntity) (Object) this);
        if (!(((CatEntity) (Object) this).getWorld().isClient)) {
            if (((CatEntity) (Object) this).isTamed()) {
                if (item instanceof CustomDyeItem && ((CatEntity) (Object) this).isOwner(player)) {
                    cat.unidye$setCustomColor(UnidyeUtils.getMaterialColor(itemStack, "leather"));
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }


    @Override
    public int unidye$getCustomColor() {
        return ((CatEntity) (Object) this).getDataTracker().get(CUSTOM_COLOR);
    }

    @Override
    public void unidye$setCustomColor(int color) {
        ((CatEntity) (Object) this).getDataTracker().set(CUSTOM_COLOR, color);
    }
}