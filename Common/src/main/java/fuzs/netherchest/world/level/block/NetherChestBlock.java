package fuzs.netherchest.world.level.block;

import fuzs.netherchest.NetherChest;
import fuzs.netherchest.config.ServerConfig;
import fuzs.netherchest.init.ModRegistry;
import fuzs.netherchest.networking.UnlimitedContainerSynchronizer;
import fuzs.netherchest.world.inventory.NetherChestMenu;
import fuzs.netherchest.world.inventory.UnlimitedContainerUtils;
import fuzs.netherchest.world.level.block.entity.NetherChestBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

@SuppressWarnings("deprecation")
public class NetherChestBlock extends EnderChestBlock {
    private static final Component DESCRIPTION_COMPONENT = Component.translatable("block.netherchest.nether_chest.description").withStyle(ChatFormatting.GOLD);

    public NetherChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity) {
                ((ChestBlockEntity) blockEntity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Container) {
                UnlimitedContainerUtils.dropContents(level, pos, (Container) blockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos above = pos.above();
            if (level.dimension() == Level.NETHER && NetherChest.CONFIG.get(ServerConfig.class).explodeInNether) {
                level.removeBlock(pos, false);
                level.explode(null, DamageSource.badRespawnPointExplosion(), null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, NetherChest.CONFIG.get(ServerConfig.class).netherExplosionStrength, true, Explosion.BlockInteraction.DESTROY);
            } else if (!NetherChest.CONFIG.get(ServerConfig.class).noBlockAbove || !level.getBlockState(above).isRedstoneConductor(level, above)) {
                MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
                if (menuProvider != null) {
                    OptionalInt containerId = player.openMenu(menuProvider);
                    if (containerId.isPresent() && player.containerMenu.containerId == containerId.getAsInt()) {
                        ((NetherChestMenu) player.containerMenu).setActualSynchronizer(new UnlimitedContainerSynchronizer((ServerPlayer) player));
                    }
                    PiglinAi.angerNearbyPiglins(player, true);
                }

            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NetherChestBlockEntity(pPos, pState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get(), NetherChestBlockEntity::lidAnimateTick) : null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return UnlimitedContainerUtils.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity) {
            ((ChestBlockEntity) blockEntity).recheckOpen();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(DESCRIPTION_COMPONENT);
    }
}
