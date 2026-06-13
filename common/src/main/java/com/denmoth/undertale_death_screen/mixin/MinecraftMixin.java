package com.denmoth.undertale_death_screen.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import com.denmoth.undertale_death_screen.DeathScreenAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void stopMusicOnDeathScreenRemoved(Screen newScreen, CallbackInfo ci) {
        if (com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon.currentBgmSoundInstance != null) {
            if (!(newScreen instanceof net.minecraft.client.gui.screens.DeathScreen) && !(newScreen instanceof net.minecraft.client.gui.screens.ConfirmScreen)) {
                Minecraft.getInstance().getSoundManager().stop((net.minecraft.client.resources.sounds.SoundInstance) com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon.currentBgmSoundInstance);
                com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon.currentBgmSoundInstance = null;
            }
        }
    }
}
