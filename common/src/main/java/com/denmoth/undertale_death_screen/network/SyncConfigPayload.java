package com.denmoth.undertale_death_screen.network;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncConfigPayload(String json) implements CustomPacketPayload {
    public static final Type<SyncConfigPayload> TYPE = new Type<>(UndertaleDeathScreenCommon.id("sync_config"));

    public static final StreamCodec<FriendlyByteBuf, SyncConfigPayload> CODEC = CustomPacketPayload.codec(
            SyncConfigPayload::write,
            SyncConfigPayload::new
    );

    public SyncConfigPayload(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.json);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
