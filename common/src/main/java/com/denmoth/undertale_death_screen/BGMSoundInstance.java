package com.denmoth.undertale_death_screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class BGMSoundInstance extends AbstractTickableSoundInstance {
    private int fadeDir;
    private int fade;

    public BGMSoundInstance(SoundEvent soundEvent) {
        super(soundEvent, Config.INSTANCE.getIndependentBgmVolume() ? SoundSource.MASTER : SoundSource.MUSIC, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.volume = 1;
        this.relative = true;
    }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        boolean onValidScreen = (client.gui != null) && (
                (client.gui.screen() instanceof net.minecraft.client.gui.screens.DeathScreen)
                || (client.gui.screen() instanceof net.minecraft.client.gui.screens.ConfirmScreen)
        );
        boolean playerDead = client.player != null && client.player.isDeadOrDying();

        if (!onValidScreen && (!playerDead || client.player == null)) {
            this.stop();
            if (UndertaleDeathScreenCommon.currentBgmSoundInstance == this) {
                UndertaleDeathScreenCommon.currentBgmSoundInstance = null;
            }
            return;
        }

        this.fade += this.fadeDir;
        this.volume = Mth.clamp((float) this.fade / 5, 0, 1);
    }

    public void fadeIn() {
        this.fade = Math.max(0, this.fade);
        this.fadeDir = 1;
    }
}
