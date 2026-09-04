package com.noveris.chatbubbles.network;

import com.noveris.chatbubbles.NoverisChatBubbles;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public record BubbleMessagePayload(UUID sender, String playerName, String message, long serverDurationMillis, int serverMaxActiveBubbles) implements CustomPacketPayload {
    public static final Type<BubbleMessagePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoverisChatBubbles.MOD_ID, "bubble_message"));
    public static final StreamCodec<ByteBuf, BubbleMessagePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.UUID, BubbleMessagePayload::sender,
            ByteBufCodecs.stringUtf8(64), BubbleMessagePayload::playerName,
            ByteBufCodecs.stringUtf8(2048), BubbleMessagePayload::message,
            ByteBufCodecs.VAR_LONG, BubbleMessagePayload::serverDurationMillis,
            ByteBufCodecs.VAR_INT, BubbleMessagePayload::serverMaxActiveBubbles,
            BubbleMessagePayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
