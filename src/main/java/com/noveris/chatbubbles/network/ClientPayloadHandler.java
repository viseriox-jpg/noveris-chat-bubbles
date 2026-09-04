package com.noveris.chatbubbles.network;

import com.noveris.chatbubbles.client.BubbleManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import com.noveris.chatbubbles.NoverisChatBubbles;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}
    public static void handle(BubbleMessagePayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        NoverisChatBubbles.LOGGER.info("Received local bubble payload for {}", payload.sender());
        context.enqueueWork(() -> BubbleManager.receive(payload));
    }
}
