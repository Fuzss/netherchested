package fuzs.netherchest.world.inventory;

import fuzs.netherchest.NetherChest;
import fuzs.netherchest.config.ServerConfig;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class UnlimitedContainer extends SimpleContainer {

    public UnlimitedContainer(int size) {
        super(size);
    }

    public UnlimitedContainer(ItemStack... itemStacks) {
        super(itemStacks);
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize() * NetherChest.CONFIG.get(ServerConfig.class).stackSizeMultiplier;
    }
}
