package com.noveris.chatbubbles.client;

import com.noveris.chatbubbles.NoverisChatBubbles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.neoforge.common.NeoForge;

/** Registers client configuration and the player render integration. */
@EventBusSubscriber(modid = NoverisChatBubbles.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerConfigScreen(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        ModList.get().getModContainerById(NoverisChatBubbles.MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(IConfigScreenFactory.class,
                        (modContainer, parent) -> new ConfigurationScreen(modContainer, parent)));

        // Register explicitly on the game bus. This guarantees RenderPlayerEvent.Post
        // is subscribed on NeoForge 21.1.248 even when automatic discovery is affected
        // by a modpack's class loading/mixin setup.
        NeoForge.EVENT_BUS.register(BubbleRenderer.class);
        NoverisChatBubbles.LOGGER.info("Registered player bubble render events");
    }

    @SubscribeEvent
    public static void addPlayerBubbleLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            PlayerRenderer renderer = (PlayerRenderer) event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new BubbleLayer(renderer));
                NoverisChatBubbles.LOGGER.info("Registered chat bubble layer for player model {}", skin);
            }
        }
    }
}
