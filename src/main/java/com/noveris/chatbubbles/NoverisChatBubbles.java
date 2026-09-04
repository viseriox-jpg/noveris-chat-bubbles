package com.noveris.chatbubbles;

import com.noveris.chatbubbles.chat.ChatCommands;
import com.noveris.chatbubbles.chat.ChatManager;
import com.noveris.chatbubbles.config.ClientConfig;
import com.noveris.chatbubbles.config.ServerConfig;
import com.noveris.chatbubbles.network.NetworkHandler;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.IEventBus;

@Mod(NoverisChatBubbles.MOD_ID)
public final class NoverisChatBubbles {
    public static final String MOD_ID = "noveris_chat_bubbles";

    public NoverisChatBubbles(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        NetworkHandler.register(modBus);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(ChatManager::onServerChat);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ChatCommands.register(event.getDispatcher());
    }
}
