package net.diemond_player.unidye.mixin;

import net.diemond_player.unidye.util.IEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin {
    @Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;setColor(Lnet/minecraft/util/DyeColor;)V"))
    private void useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        IEntityAccessor sheep = (IEntityAccessor) entity;
        sheep.unidye$setCustomColor(0xFFFFFF);
        sheep.unidye$setSecondaryCustomColor(0xFFFFFF);
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void useOnEntityCheck(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) throws NoSuchFieldException {
        SheepEntity sheepEntity;
        if (entity instanceof SheepEntity && (sheepEntity = (SheepEntity)entity).isAlive() && !sheepEntity.isSheared()) {
            sheepEntity.getWorld().playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            if (!user.getWorld().isClient) {
                sheepEntity.setColor(((DyeItem) (Object) this).getColor());
                IEntityAccessor sheep = (IEntityAccessor) entity;
                sheep.unidye$setCustomColor(0xFFFFFF);
                sheep.unidye$setSecondaryCustomColor(0xFFFFFF);
                stack.decrement(1);
            }
            cir.setReturnValue(ActionResult.success(user.getWorld().isClient));
        }
    }

    @Inject(method = "useOnSign", at = @At("HEAD"), cancellable = true)
    private void useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) throws NoSuchFieldException {
        IEntityAccessor iEntityAccessor = (IEntityAccessor) signBlockEntity;
        if(front){
            if(iEntityAccessor.unidye$getCustomColor() != 0xFFFFFF){
                iEntityAccessor.unidye$setCustomColor(0xFFFFFF);
                if (signBlockEntity.changeText(text -> text.withColor(((DyeItem) (Object) this).getColor()), front)) {
                    iEntityAccessor.unidye$setCustomColor(0xFFFFFF);
                    signBlockEntity.markDirty();
                    world.updateListeners(signBlockEntity.getPos(), world.getBlockState(signBlockEntity.getPos()), world.getBlockState(signBlockEntity.getPos()), Block.NOTIFY_LISTENERS);
                    world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    cir.setReturnValue(true);
                }
                signBlockEntity.markDirty();
                world.updateListeners(signBlockEntity.getPos(), world.getBlockState(signBlockEntity.getPos()), world.getBlockState(signBlockEntity.getPos()), Block.NOTIFY_LISTENERS);
                world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                cir.setReturnValue(true);
            }
        }else{
            if(iEntityAccessor.unidye$getSecondaryCustomColor() != 0xFFFFFF){
                iEntityAccessor.unidye$setSecondaryCustomColor(0xFFFFFF);
                if (signBlockEntity.changeText(text -> text.withColor(((DyeItem) (Object) this).getColor()), front)) {
                    iEntityAccessor.unidye$setSecondaryCustomColor(0xFFFFFF);
                    signBlockEntity.markDirty();
                    world.updateListeners(signBlockEntity.getPos(), world.getBlockState(signBlockEntity.getPos()), world.getBlockState(signBlockEntity.getPos()), Block.NOTIFY_LISTENERS);
                    world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    cir.setReturnValue(true);
                }
                signBlockEntity.markDirty();
                world.updateListeners(signBlockEntity.getPos(), world.getBlockState(signBlockEntity.getPos()), world.getBlockState(signBlockEntity.getPos()), Block.NOTIFY_LISTENERS);
                world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                cir.setReturnValue(true);
            }
        }
    }
}
