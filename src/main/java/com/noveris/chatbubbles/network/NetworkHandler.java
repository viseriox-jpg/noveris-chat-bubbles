package com.noveris.chatbubbles.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import com.noveris.chatbubbles.NoverisChatBubbles;

public final class NetworkHandler {
    private static IEventBus modBus;
    private NetworkHandler() {}
    public static void register() {
        modBus = ModList.get().getModContainerById(NoverisChatBubbles.MOD_ID).orElseThrow().getEventBus();
        modBus.addListener(NetworkHandler::registerPayloads);
    }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(BubbleMessagePayload.TYPE, BubbleMessagePayload.STREAM_CODEC, ClientPayloadHandler::handle);
    }
    public static void sendTo(ServerPlayer player, BubbleMessagePayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
