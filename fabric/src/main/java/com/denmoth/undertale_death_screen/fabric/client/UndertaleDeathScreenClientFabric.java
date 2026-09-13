package com.denmoth.undertale_death_screen.fabric.client;

import com.denmoth.undertale_death_screen.Config;
import com.denmoth.undertale_death_screen.CustomHeartDetector;
import com.denmoth.undertale_death_screen.DynamicHeartTextureManager;
import com.denmoth.undertale_death_screen.network.SyncConfigPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class UndertaleDeathScreenClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncConfigPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                Config.updateFromServer(payload.json());
            });
        });

        // Invalidate custom heart cache when resource packs reload
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.fromNamespaceAndPath("undertale_death_screen", "heart_texture_cache");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        DynamicHeartTextureManager.cleanup();
                        CustomHeartDetector.invalidate();
                    }
                }
        );
    }
}
