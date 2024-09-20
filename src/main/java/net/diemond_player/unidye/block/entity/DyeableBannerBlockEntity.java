package net.diemond_player.unidye.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.diemond_player.unidye.block.UnidyeBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DyeableBannerBlockEntity extends BlockEntity implements Nameable {
    public static final int MAX_PATTERN_COUNT = 6;
    public static final String PATTERNS_KEY = "Patterns";
    public static final String PATTERN_KEY = "Pattern";
    public static final String COLOR_KEY = "Color";
    @Nullable
    private Text customName;
    @Nullable
    private NbtList patternListNbt;
    public static final int DEFAULT_COLOR = 16777215;
    public int color = DEFAULT_COLOR;
    private List<Pair<RegistryEntry<BannerPattern>, ?>> patterns;

    public DyeableBannerBlockEntity(BlockPos pos, BlockState state) {
        super(UnidyeBlockEntities.DYEABLE_BANNER_BE, pos, state);
    }

    @Override
    public void markDirty() {
        if (this.world != null) {
            markDirty(this.world, this.pos, this.getCachedState());
        }
    }

    public static int getColor(BlockView world, BlockPos pos) {
        if (world == null) {
            return DyeableBannerBlockEntity.DEFAULT_COLOR;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DyeableBannerBlockEntity dyeableBlockEntity) {
            return dyeableBlockEntity.color;
        } else {
            return DyeableBannerBlockEntity.DEFAULT_COLOR;
        }
    }

    @Nullable
    public static NbtList getPatternListNbt(ItemStack stack) {
        NbtList nbtList = null;
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) {
            nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).copy();
        }
        return nbtList;
    }

    public void readFrom(ItemStack stack) {
        this.patternListNbt = BannerBlockEntity.getPatternListNbt(stack);
        this.patterns = null;
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return Text.translatable("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.patternListNbt != null) {
            nbt.put(PATTERNS_KEY, this.patternListNbt);
        }
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        if (color != DEFAULT_COLOR) {
            nbt.putInt("color", color);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        this.patternListNbt = nbt.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        this.patterns = null;
        if (nbt.getInt("color") == 0) {
            color = DEFAULT_COLOR;
        } else {
            color = nbt.getInt("color");
        }
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public static int getPatternCount(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY)) {
            return nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).size();
        }
        return 0;
    }

    public List<Pair<RegistryEntry<BannerPattern>, ?>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = this.getPatternsFromNbt(this.patternListNbt);
        }
        return this.patterns;
    }

    public List<Pair<RegistryEntry<BannerPattern>, ?>> getPatternsFromNbt(@Nullable NbtList patternListNbt) {
        ArrayList<Pair<RegistryEntry<BannerPattern>, ?>> list = Lists.newArrayList();
        list.add(Pair.of(Registries.BANNER_PATTERN.entryOf(BannerPatterns.BASE), this.color));
        if (patternListNbt != null) {
            for (int i = 0; i < patternListNbt.size(); ++i) {
                NbtCompound nbtCompound = patternListNbt.getCompound(i);
                RegistryEntry<BannerPattern> registryEntry = BannerPattern.byId(nbtCompound.getString(PATTERN_KEY));
                if (registryEntry == null) continue;
                int j = nbtCompound.getInt(COLOR_KEY);
                list.add(Pair.of(registryEntry, DyeColor.byId(j)));
            }
        }
        return list;
    }

    public static void loadFromItemStack(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null || !nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) {
            return;
        }
        NbtList nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return;
        }
        nbtList.remove(nbtList.size() - 1);
        if (nbtList.isEmpty()) {
            nbtCompound.remove(PATTERNS_KEY);
        }
        BlockItem.setBlockEntityNbt(stack, UnidyeBlockEntities.DYEABLE_BANNER_BE, nbtCompound);
    }

    public ItemStack getPickStack() {
        ItemStack itemStack = new ItemStack(UnidyeBlocks.CUSTOM_BANNER);
        DyeableBannerBlockEntity blockEntity = UnidyeBlockEntities.DYEABLE_BANNER_BE.get(world,pos);
        int color = DyeableBannerBlockEntity.DEFAULT_COLOR;
        if(blockEntity != null){
            color = blockEntity.color;
        }
        NbtCompound subNbt = itemStack.getOrCreateSubNbt("display");
        subNbt.putInt("color", color);
        if (this.patternListNbt != null && !this.patternListNbt.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put(PATTERNS_KEY, this.patternListNbt.copy());
            BlockItem.setBlockEntityNbt(itemStack, this.getType(), nbtCompound);
        }
        if (this.customName != null) {
            itemStack.setCustomName(this.customName);
        }
        return itemStack;
    }
}
