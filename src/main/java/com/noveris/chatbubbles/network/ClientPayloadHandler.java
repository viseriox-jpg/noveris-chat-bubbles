package com.noveris.chatbubbles.network;

import com.noveris.chatbubbles.client.BubbleManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}
    public static void handle(BubbleMessagePayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        context.enqueueWork(() -> BubbleManager.receive(payload));
    }
}
