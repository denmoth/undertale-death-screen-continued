package com.denmoth.undertale_death_screen.mixin;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "setScreenAndShow", at = @At("HEAD"))
    private void stopMusicOnDeathScreenRemoved(Screen newScreen, CallbackInfo ci) {
        undertale_death_animation$stopBackgroundMusicIfDone(newScreen);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stopMusicOnRespawn(CallbackInfo ci) {
        undertale_death_animation$stopBackgroundMusicIfDone(Minecraft.getInstance().gui.screen());
    }

    @Unique
    private void undertale_death_animation$stopBackgroundMusicIfDone(@Nullable Screen screen) {
        SoundInstance bgm = (SoundInstance) UndertaleDeathScreenCommon.currentBgmSoundInstance;
        if (bgm == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        boolean onDeathScreen = screen instanceof DeathScreen || screen instanceof ConfirmScreen;

        if (!onDeathScreen && (player == null || !player.isDeadOrDying())) {
            minecraft.getSoundManager().stop(bgm);
            UndertaleDeathScreenCommon.currentBgmSoundInstance = null;
        }
    }
}
