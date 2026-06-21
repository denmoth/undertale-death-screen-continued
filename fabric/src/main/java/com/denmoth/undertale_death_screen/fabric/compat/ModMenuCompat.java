package com.denmoth.undertale_death_screen.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return UndertaleDeathScreenCommon::getConfigScreen;
    }
}
