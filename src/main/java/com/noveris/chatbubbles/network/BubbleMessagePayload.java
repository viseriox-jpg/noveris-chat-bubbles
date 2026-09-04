package com.noveris.chatbubbles.network;

import com.noveris.chatbubbles.NoverisChatBubbles;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public record BubbleMessagePayload(UUID sender, Component playerName, String message, long serverDurationMillis, int serverMaxActiveBubbles) implements CustomPacketPayload {
    public static final Type<BubbleMessagePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoverisChatBubbles.MOD_ID, "bubble_message"));
    private static final StreamCodec<RegistryFriendlyByteBuf, UUID> UUID_CODEC = new StreamCodec<>() {
        @Override public UUID decode(RegistryFriendlyByteBuf buffer) { return new UUID(buffer.readLong(), buffer.readLong()); }
        @Override public void encode(RegistryFriendlyByteBuf buffer, UUID value) { buffer.writeLong(value.getMostSignificantBits()); buffer.writeLong(value.getLeastSignificantBits()); }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, BubbleMessagePayload> STREAM_CODEC = StreamCodec.composite(
            UUID_CODEC, BubbleMessagePayload::sender,
            ComponentSerialization.STREAM_CODEC, BubbleMessagePayload::playerName,
            ByteBufCodecs.stringUtf8(2048), BubbleMessagePayload::message,
            ByteBufCodecs.VAR_LONG, BubbleMessagePayload::serverDurationMillis,
            ByteBufCodecs.VAR_INT, BubbleMessagePayload::serverMaxActiveBubbles,
            BubbleMessagePayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
