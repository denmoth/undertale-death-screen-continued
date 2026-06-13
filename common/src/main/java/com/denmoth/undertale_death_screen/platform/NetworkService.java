package com.denmoth.undertale_death_screen.platform;

import net.minecraft.server.level.ServerPlayer;

public interface NetworkService {
    void sendConfigSync(ServerPlayer player);
}
