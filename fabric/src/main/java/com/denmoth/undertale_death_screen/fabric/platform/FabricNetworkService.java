package com.denmoth.undertale_death_screen.fabric.platform;

import com.denmoth.undertale_death_screen.Config;
import com.denmoth.undertale_death_screen.network.SyncConfigPayload;
import com.denmoth.undertale_death_screen.platform.NetworkService;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkService implements NetworkService {
    @Override
    public void sendConfigSync(ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, SyncConfigPayload.TYPE)) {
            String json = new Gson().toJson(Config.INSTANCE);
            ServerPlayNetworking.send(player, new SyncConfigPayload(json));
        }
    }
}
