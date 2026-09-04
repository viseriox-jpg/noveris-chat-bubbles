package com.noveris.chatbubbles.client;

import com.noveris.chatbubbles.NoverisChatBubbles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Registers the generic NeoForge config UI only on physical clients. */
@EventBusSubscriber(modid = NoverisChatBubbles.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerConfigScreen(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        ModList.get().getModContainerById(NoverisChatBubbles.MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(IConfigScreenFactory.class,
                        (modContainer, parent) -> new ConfigurationScreen(modContainer, parent)));
    }
}
