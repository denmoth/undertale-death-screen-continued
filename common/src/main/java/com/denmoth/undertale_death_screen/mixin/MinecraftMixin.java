package com.denmoth.undertale_death_screen.mixin;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "setScreenAndShow", at = @At("HEAD"))
    private void stopMusicOnDeathScreenRemoved(Screen newScreen, CallbackInfo ci) {
        if (UndertaleDeathScreenCommon.currentBgmSoundInstance != null) {
            boolean keepPlaying = (newScreen instanceof DeathScreen) || (newScreen instanceof ConfirmScreen);
            if (!keepPlaying) {
                Minecraft.getInstance().getSoundManager().stop((SoundInstance) UndertaleDeathScreenCommon.currentBgmSoundInstance);
                UndertaleDeathScreenCommon.currentBgmSoundInstance = null;
            }
        }
    }
}
