package fuzs.netherchest.world.level.block.entity;

import fuzs.netherchest.init.ModRegistry;
import fuzs.netherchest.world.inventory.NetherChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class NetherChestBlockEntity extends ChestBlockEntity {
    private final ContainerOpenersCounter openersCounter;

    public NetherChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get(), blockPos, blockState);
        this.setItems(NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY));
        this.openersCounter = new ContainerOpenersCounter() {

            @Override
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                level.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }

            @Override
            protected void onClose(Level level, BlockPos pos, BlockState state) {
                level.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
                level.blockEvent(pos, state.getBlock(), 1, openCount);
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof NetherChestMenu netherChestMenu) {
                    return netherChestMenu.getContainer() == NetherChestBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> list, boolean saveEmpty) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = list.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemStack.save(compoundTag);
                compoundTag.putInt("Count", itemStack.getCount());
                listTag.add(compoundTag);
            }
        }

        if (!listTag.isEmpty() || saveEmpty) {
            tag.put("Items", listTag);
        }

        return tag;
    }

    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> list) {
        ListTag listTag = tag.getList("Items", 10);

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j >= 0 && j < list.size()) {
                ItemStack itemStack = ItemStack.of(compoundTag);
                itemStack.setCount(compoundTag.getInt("Count"));
                list.set(j, itemStack);
            }
        }

    }

    @Override
    public int getContainerSize() {
        return 54;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.nether_chest");
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize() * 8;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.setItems(NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY));
        if (!this.tryLoadLootTable(tag)) {
            loadAllItems(tag, this.getItems());
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.remove("Items");
        if (!this.trySaveLootTable(tag)) {
            saveAllItems(tag, this.getItems(), true);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new NetherChestMenu(containerId, inventory, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
}
