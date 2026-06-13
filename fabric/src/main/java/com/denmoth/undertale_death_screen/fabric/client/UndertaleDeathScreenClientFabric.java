package com.denmoth.undertale_death_screen.fabric.client;

import com.denmoth.undertale_death_screen.Config;
import com.denmoth.undertale_death_screen.network.SyncConfigPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class UndertaleDeathScreenClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncConfigPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                Config.updateFromServer(payload.json());
            });
        });
    }
}
