package com.noveris.chatbubbles.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.IEventBus;

public final class NetworkHandler {
    private NetworkHandler() {}
    public static void register(IEventBus modBus) {
        modBus.addListener(NetworkHandler::registerPayloads);
    }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(BubbleMessagePayload.TYPE, BubbleMessagePayload.STREAM_CODEC, ClientPayloadHandler::handle);
    }
    public static void sendTo(ServerPlayer player, BubbleMessagePayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
