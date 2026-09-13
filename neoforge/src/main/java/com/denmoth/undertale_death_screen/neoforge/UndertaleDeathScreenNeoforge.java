package com.denmoth.undertale_death_screen.neoforge;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.denmoth.undertale_death_screen.UndertaleDeathScreenBase;
import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;

import java.nio.file.Path;
import java.util.function.Supplier;

import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.minecraft.resources.Identifier;
import com.denmoth.undertale_death_screen.CustomHeartDetector;
import com.denmoth.undertale_death_screen.DynamicHeartTextureManager;

@Mod(UndertaleDeathScreenCommon.MOD_ID)
public class UndertaleDeathScreenNeoforge {
    public UndertaleDeathScreenNeoforge(IEventBus eventBus, ModContainer container) {
        UndertaleDeathScreenCommon.init(new Impl());
        Impl.register(eventBus);

        if (UndertaleDeathScreenCommon.isConfigSupported()) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
                // neoforge changed the signature some time, no idea when but this should support both

                public @NotNull Screen createScreen(@NotNull ModContainer container, @NotNull Screen parent) {
                    return getConfigScreen(parent);
                }
                public @NotNull Screen createScreen(@NotNull Minecraft client, @NotNull Screen parent) {
                    return getConfigScreen(parent);
                }
            });
        }
    }

    private Screen getConfigScreen(Screen parent) {
        Screen screen = UndertaleDeathScreenCommon.getConfigScreen(parent);
        return screen == null ? parent : screen;
    }

    public static class Impl implements UndertaleDeathScreenBase {
        private static final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, UndertaleDeathScreenCommon.MOD_ID);

        public static void register(IEventBus eventBus) {
            SOUND_EVENT_REGISTRY.register(eventBus);
            eventBus.addListener(Impl::onAddReloadListeners);
        }

        private static void onAddReloadListeners(AddClientReloadListenersEvent event) {
            event.addListener(
                    Identifier.fromNamespaceAndPath("undertale_death_screen", "heart_texture_cache"),
                    new net.minecraft.server.packs.resources.SimplePreparableReloadListener<Void>() {
                        @Override
                        protected Void prepare(net.minecraft.server.packs.resources.ResourceManager manager, net.minecraft.util.profiling.ProfilerFiller profiler) {
                            return null;
                        }
                        @Override
                        protected void apply(Void prepared, net.minecraft.server.packs.resources.ResourceManager manager, net.minecraft.util.profiling.ProfilerFiller profiler) {
                            DynamicHeartTextureManager.cleanup();
                            CustomHeartDetector.invalidate();
                        }
                    }
            );
        }

        @Override
        public Supplier<SoundEvent> registerSoundEvent(String path) {
            return SOUND_EVENT_REGISTRY.register(
                    path,
                    () -> SoundEvent.createVariableRangeEvent(UndertaleDeathScreenCommon.id(path))
            );
        }

        @Override
        public boolean isModLoaded(String id) {
            return ModList.get().isLoaded(id);
        }

        @Override
        public Path getConfigDir() {
            return FMLPaths.CONFIGDIR.get();
        }
    }
}
