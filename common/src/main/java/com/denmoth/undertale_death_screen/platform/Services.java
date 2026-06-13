package com.denmoth.undertale_death_screen.platform;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;
import java.util.ServiceLoader;

public class Services {
    public static final NetworkService NETWORK = load(NetworkService.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
