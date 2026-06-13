package com.denmoth.undertale_death_screen.compat.impl;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.denmoth.undertale_death_screen.Config;
import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;
import com.denmoth.undertale_death_screen.compat.ClothConfigCompatBase;

public class ClothConfigCompat implements ClothConfigCompatBase {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal(UndertaleDeathScreenCommon.MOD_NAME))
                .setSavingRunnable(Config.INSTANCE::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.empty()); // doesn't show when there's only 1 category anyway
        general.addEntry(entryBuilder.startEnumSelector(
                                UndertaleDeathScreenCommon.translatable("config.shard_render_style"),
                                Config.ShardRenderStyle.class,
                                Config.INSTANCE.getStyle()
                        ).setDefaultValue(Config.getDefault().getStyle())
                        .setEnumNameProvider((e) -> UndertaleDeathScreenCommon.translatable(
                                "config.shard_render_style." + ((Config.ShardRenderStyle) e).getSerializedName())
                        )
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.shard_render_style.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setStyle)
                        .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                                UndertaleDeathScreenCommon.translatable("config.music_turnoff"),
                                Config.INSTANCE.getShouldStopSound()
                        ).setDefaultValue(Config.getDefault().getShouldStopSound())
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.music_turnoff.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setShouldStopSound)
                        .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.determination"),
                        Config.INSTANCE.getDetermination()
                ).setDefaultValue(Config.getDefault().getDetermination())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.determination.ttp"))
                .setSaveConsumer(Config.INSTANCE::setDetermination)
                .build());
        BooleanListEntry centerHeartToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.centered_heart"),
                        Config.INSTANCE.getCenteredHeart()
                ).setDefaultValue(Config.getDefault().getCenteredHeart())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart.ttp"))
                .setSaveConsumer(Config.INSTANCE::setCenteredHeart)
                .build();
        general.addEntry(centerHeartToggle);
        BooleanListEntry centerHeartAnimationToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.centered_heart_anim"),
                        Config.INSTANCE.getCenteredHeartAnimation()
                ).setDefaultValue(Config.getDefault().getCenteredHeartAnimation())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart_anim.ttp"))
                .setSaveConsumer(Config.INSTANCE::setCenteredHeartAnimation)
                .setDisplayRequirement(Requirement.isTrue(centerHeartToggle))
                .build();
        general.addEntry(centerHeartAnimationToggle);
        general.addEntry(entryBuilder.startDoubleField(
                                UndertaleDeathScreenCommon.translatable("config.centered_heart_speed"),
                                Config.INSTANCE.getCenteredHeartSpeed()
                        ).setDefaultValue(Config.getDefault().getCenteredHeartSpeed())
                        .setMin(0.1)
                        .setMax(1)
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.centered_heart_speed.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setCenteredHeartSpeed)
                        .setDisplayRequirement(Requirement.all(Requirement.isTrue(centerHeartAnimationToggle), Requirement.isTrue(centerHeartToggle)))
                        .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.dynamicHeart"),
                        Config.INSTANCE.getDynamicHeart()
                ).setDefaultValue(Config.getDefault().getDynamicHeart())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.dynamicHeart.ttp"))
                .setSaveConsumer(Config.INSTANCE::setDynamicHeart)
                .build());
        general.addEntry(entryBuilder.startDoubleField(
                                UndertaleDeathScreenCommon.translatable("config.background_fade_speed"),
                                Config.INSTANCE.getBackgroundFadeSpeed()
                        ).setDefaultValue(Config.getDefault().getBackgroundFadeSpeed())
                        .setMin(0)
                        .setMax(1)
                        .setTooltip(UndertaleDeathScreenCommon.translatable("config.background_fade_speed.ttp"))
                        .setSaveConsumer(Config.INSTANCE::setBackgroundFadeSpeed)
                        .build()
        );
        BooleanListEntry vanillaFadeInToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.vanilla_fade_in"),
                        Config.INSTANCE.getVanillaFadeIn()
                ).setDefaultValue(Config.getDefault().getVanillaFadeIn())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.vanilla_fade_in.ttp"))
                .setSaveConsumer(Config.INSTANCE::setVanillaFadeIn)
                .build();
        general.addEntry(vanillaFadeInToggle);
        general.addEntry(entryBuilder.startIntField(
                        UndertaleDeathScreenCommon.translatable("config.vanilla_fade_in_duration"),
                        Config.INSTANCE.getVanillaFadeInDuration()
                ).setDefaultValue(Config.getDefault().getVanillaFadeInDuration())
                .setMin(0)
                .setMax(200)
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.vanilla_fade_in_duration.ttp"))
                .setSaveConsumer(Config.INSTANCE::setVanillaFadeInDuration)
                .setDisplayRequirement(Requirement.isTrue(vanillaFadeInToggle))
                .build()
        );
        BooleanListEntry textFadeInToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.text_fade_in"),
                        Config.INSTANCE.getTextFadeIn()
                ).setDefaultValue(Config.getDefault().getTextFadeIn())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.text_fade_in.ttp"))
                .setSaveConsumer(Config.INSTANCE::setTextFadeIn)
                .build();
        general.addEntry(textFadeInToggle);
        general.addEntry(entryBuilder.startIntField(
                        UndertaleDeathScreenCommon.translatable("config.text_fade_in_duration"),
                        Config.INSTANCE.getTextFadeInDuration()
                ).setDefaultValue(Config.getDefault().getTextFadeInDuration())
                .setMin(0)
                .setMax(200)
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.text_fade_in_duration.ttp"))
                .setSaveConsumer(Config.INSTANCE::setTextFadeInDuration)
                .setDisplayRequirement(Requirement.isTrue(textFadeInToggle))
                .build()
        );

        BooleanListEntry fadeToVanillaScreenToggle = entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.fade_to_vanilla_screen"),
                        Config.INSTANCE.getFadeToVanillaScreen()
                ).setDefaultValue(Config.getDefault().getFadeToVanillaScreen())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.fade_to_vanilla_screen.ttp"))
                .setSaveConsumer(Config.INSTANCE::setFadeToVanillaScreen)
                .build();
        general.addEntry(fadeToVanillaScreenToggle);
        general.addEntry(entryBuilder.startDoubleField(
                        UndertaleDeathScreenCommon.translatable("config.fade_to_vanilla_speed"),
                        Config.INSTANCE.getFadeToVanillaSpeed()
                ).setDefaultValue(Config.getDefault().getFadeToVanillaSpeed())
                .setMin(0.01)
                .setMax(1)
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.fade_to_vanilla_speed.ttp"))
                .setSaveConsumer(Config.INSTANCE::setFadeToVanillaSpeed)
                .setDisplayRequirement(Requirement.isTrue(fadeToVanillaScreenToggle))
                .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.fixed_animation_rate"),
                        Config.INSTANCE.getFixedAnimationRate()
                ).setDefaultValue(Config.getDefault().getFixedAnimationRate())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.fixed_animation_rate.ttp"))
                .setSaveConsumer(Config.INSTANCE::setFixedAnimationRate)
                .build()
        );
        general.addEntry(entryBuilder.startBooleanToggle(
                        UndertaleDeathScreenCommon.translatable("config.disable_buttons_before_animation"),
                        Config.INSTANCE.getDisableButtonsBeforeAnimation()
                ).setDefaultValue(Config.getDefault().getDisableButtonsBeforeAnimation())
                .setTooltip(UndertaleDeathScreenCommon.translatable("config.disable_buttons_before_animation.ttp"))
                .setSaveConsumer(Config.INSTANCE::setDisableButtonsBeforeAnimation)
                .build()
        );

        return builder.build();
    }
}
