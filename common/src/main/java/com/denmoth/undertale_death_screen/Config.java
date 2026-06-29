package com.denmoth.undertale_death_screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = UndertaleDeathScreenCommon.impl.getConfigDir().resolve(UndertaleDeathScreenCommon.MOD_ID + ".json");

    public static final Config INSTANCE = load();

    private ShardRenderStyle style = ShardRenderStyle.ROTATION;
    private boolean musicTurnoff = true;
    private boolean determination = true;
    private boolean centeredHeart = false;
    private boolean centeredHeartAnimation = false;
    private double centeredHeartSpeed = 0.15;
    private double backgroundFadeSpeed = 1;
    private boolean dynamicHeart = true;
    private boolean vanillaFadeIn = true;
    private int vanillaFadeInDuration = 20;
    private boolean textFadeIn = false;
    private int textFadeInDuration = 40;
    private boolean fadeToVanillaScreen = false;
    private double fadeToVanillaSpeed = 0.05;
    private boolean fixedAnimationRate = true;
    private boolean disableButtonsBeforeAnimation = true;
    private boolean disableVanillaRedTint = false;
    private boolean independentBgmVolume = true;

    private Config() {
    }

    public static Config getDefault() {
        return new Config();
    }

    private static Config load() {
        Config config = null;
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                    config = GSON.fromJson(reader, Config.class);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            UndertaleDeathScreenCommon.logger.error("Failed to load configuration file. Using default values.", e);
        }

        if (config == null) {
            config = getDefault();
        }

        config.centeredHeartSpeed = Math.max(0.1, Math.min(1.0, config.centeredHeartSpeed));
        config.save();
        return config;
    }

    public static void updateFromServer(String json) {
        try {
            Config serverConfig = GSON.fromJson(json, Config.class);
            if (serverConfig != null) {
                INSTANCE.style = serverConfig.style;
                INSTANCE.musicTurnoff = serverConfig.musicTurnoff;
                INSTANCE.determination = serverConfig.determination;
                INSTANCE.centeredHeart = serverConfig.centeredHeart;
                INSTANCE.centeredHeartAnimation = serverConfig.centeredHeartAnimation;
                INSTANCE.centeredHeartSpeed = serverConfig.centeredHeartSpeed;
                INSTANCE.backgroundFadeSpeed = serverConfig.backgroundFadeSpeed;
                INSTANCE.dynamicHeart = serverConfig.dynamicHeart;
                INSTANCE.vanillaFadeIn = serverConfig.vanillaFadeIn;
                INSTANCE.vanillaFadeInDuration = serverConfig.vanillaFadeInDuration;
                INSTANCE.textFadeIn = serverConfig.textFadeIn;
                INSTANCE.textFadeInDuration = serverConfig.textFadeInDuration;
                INSTANCE.fadeToVanillaScreen = serverConfig.fadeToVanillaScreen;
                INSTANCE.fadeToVanillaSpeed = serverConfig.fadeToVanillaSpeed;
                INSTANCE.fixedAnimationRate = serverConfig.fixedAnimationRate;
                INSTANCE.disableButtonsBeforeAnimation = serverConfig.disableButtonsBeforeAnimation;
                INSTANCE.disableVanillaRedTint = serverConfig.disableVanillaRedTint;
                INSTANCE.independentBgmVolume = serverConfig.independentBgmVolume;
                UndertaleDeathScreenCommon.logger.info("Successfully synced configuration from server.");
            }
        } catch (JsonSyntaxException e) {
            UndertaleDeathScreenCommon.logger.error("Failed to parse configuration from server.", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            UndertaleDeathScreenCommon.logger.error("Failed to save configuration file:", e);
        }
    }

    public boolean getDynamicHeart() {
        return dynamicHeart;
    }

    public void setDynamicHeart(boolean value) {
        dynamicHeart = value;
    }

    public boolean getDetermination() {
        return determination;
    }

    public void setDetermination(boolean value) {
        determination = value;
    }

    public double getCenteredHeartSpeed() {
        return centeredHeartSpeed;
    }

    public void setCenteredHeartSpeed(double value) {
        centeredHeartSpeed = value;
    }

    public boolean getCenteredHeartAnimation() {
        return centeredHeartAnimation;
    }

    public void setCenteredHeartAnimation(boolean value) {
        centeredHeartAnimation = value;
    }

    public boolean getCenteredHeart() {
        return centeredHeart;
    }

    public void setCenteredHeart(boolean value) {
        centeredHeart = value;
    }

    public ShardRenderStyle getStyle() {
        return style;
    }

    public void setStyle(ShardRenderStyle style) {
        this.style = style;
    }

    public boolean getShouldStopSound() {
        return musicTurnoff;
    }
    public void setShouldStopSound(boolean musicTurnoff) {
        this.musicTurnoff = musicTurnoff;
    }

    public double getBackgroundFadeSpeed() {
        return backgroundFadeSpeed;
    }
    public void setBackgroundFadeSpeed(double backgroundFadeSpeed) {
        this.backgroundFadeSpeed = backgroundFadeSpeed;
    }

    public boolean getVanillaFadeIn() {
        return vanillaFadeIn;
    }
    public void setVanillaFadeIn(boolean vanillaFadeIn) {
        this.vanillaFadeIn = vanillaFadeIn;
    }

    public int getVanillaFadeInDuration() {
        return vanillaFadeInDuration;
    }
    public void setVanillaFadeInDuration(int vanillaFadeInDuration) {
        this.vanillaFadeInDuration = vanillaFadeInDuration;
    }

    public boolean getTextFadeIn() {
        return textFadeIn;
    }
    public void setTextFadeIn(boolean textFadeIn) {
        this.textFadeIn = textFadeIn;
    }

    public int getTextFadeInDuration() {
        return textFadeInDuration;
    }
    public void setTextFadeInDuration(int textFadeInDuration) {
        this.textFadeInDuration = textFadeInDuration;
    }

    public boolean getFadeToVanillaScreen() {
        return fadeToVanillaScreen;
    }
    public void setFadeToVanillaScreen(boolean fadeToVanillaScreen) {
        this.fadeToVanillaScreen = fadeToVanillaScreen;
    }

    public double getFadeToVanillaSpeed() {
        return fadeToVanillaSpeed;
    }
    public void setFadeToVanillaSpeed(double fadeToVanillaSpeed) {
        this.fadeToVanillaSpeed = fadeToVanillaSpeed;
    }

    public boolean getFixedAnimationRate() {
        return fixedAnimationRate;
    }
    public void setFixedAnimationRate(boolean fixedAnimationRate) {
        this.fixedAnimationRate = fixedAnimationRate;
    }

    public boolean getDisableButtonsBeforeAnimation() {
        return disableButtonsBeforeAnimation;
    }
    public void setDisableButtonsBeforeAnimation(boolean disableButtonsBeforeAnimation) {
        this.disableButtonsBeforeAnimation = disableButtonsBeforeAnimation;
    }

    public boolean getDisableVanillaRedTint() {
        return disableVanillaRedTint;
    }
    public void setDisableVanillaRedTint(boolean disableVanillaRedTint) {
        this.disableVanillaRedTint = disableVanillaRedTint;
    }

    public boolean getIndependentBgmVolume() {
        return independentBgmVolume;
    }
    public void setIndependentBgmVolume(boolean independentBgmVolume) {
        this.independentBgmVolume = independentBgmVolume;
    }


    public enum ShardRenderStyle implements StringRepresentable {
        ANIMATED("animated"),
        ROTATION("rotated");

        private final String name;

        ShardRenderStyle(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
