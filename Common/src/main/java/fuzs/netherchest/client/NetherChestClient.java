package fuzs.netherchest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.netherchest.NetherChest;
import fuzs.netherchest.client.gui.screens.inventory.NetherChestScreen;
import fuzs.netherchest.init.ModRegistry;
import fuzs.netherchest.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

public class NetherChestClient implements ClientModConstructor {
    public static final Material NETHER_CHEST_LOCATION = new Material(Sheets.CHEST_SHEET, NetherChest.id("entity/chest/nether"));

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get(), ChestRenderer::new);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.NETHER_CHEST_MENU_TYPE.get(), NetherChestScreen::new);
    }

    @Override
    public void onRegisterBuiltinModelItemRenderers(BuiltinModelItemRendererContext context) {
        context.register(ModRegistry.NETHER_CHEST_BLOCK.get(), new DynamicBuiltinModelItemRenderer() {
            private NetherChestBlockEntity netherChest;

            @Override
            public void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.netherChest, matrices, vertexConsumers, light, overlay);
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                this.netherChest = new NetherChestBlockEntity(BlockPos.ZERO, ModRegistry.NETHER_CHEST_BLOCK.get().defaultBlockState());
            }
        });
    }
}
