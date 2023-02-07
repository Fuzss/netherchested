package fuzs.netherchest;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class NetherChestFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(NetherChest.MOD_ID).accept(new NetherChest());
    }
}
