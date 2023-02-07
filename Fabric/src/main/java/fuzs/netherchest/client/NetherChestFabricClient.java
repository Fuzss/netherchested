package fuzs.netherchest.client;

import fuzs.netherchest.NetherChest;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.fabricmc.api.ClientModInitializer;

public class NetherChestFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFactories.INSTANCE.clientModConstructor(NetherChest.MOD_ID).accept(new NetherChestClient());
    }
}
