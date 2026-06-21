package com.denmoth.undertale_death_screen.fabric;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import com.denmoth.undertale_death_screen.UndertaleDeathScreenBase;
import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;

import java.nio.file.Path;
import java.util.function.Supplier;


public class UndertaleDeathScreenFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UndertaleDeathScreenCommon.init(new Impl());

        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(
                com.denmoth.undertale_death_screen.network.SyncConfigPayload.TYPE,
                com.denmoth.undertale_death_screen.network.SyncConfigPayload.CODEC
        );

        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            com.denmoth.undertale_death_screen.platform.Services.NETWORK.sendConfigSync(handler.player);
        });
    }

    public static final class Impl implements UndertaleDeathScreenBase {

        @Override
        public Supplier<SoundEvent> registerSoundEvent(String path) {
            Identifier location = UndertaleDeathScreenCommon.id(path);
            SoundEvent event = Registry.register(
                    BuiltInRegistries.SOUND_EVENT,
                    location,
                    SoundEvent.createVariableRangeEvent(location)
            );
            return () -> event;
        }

        @Override
        public boolean isModLoaded(String id) {
            return FabricLoader.getInstance().isModLoaded(id);
        }

        @Override
        public Path getConfigDir() {
            return FabricLoader.getInstance().getConfigDir();
        }
    }
}
