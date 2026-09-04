package com.noveris.chatbubbles.chat;

import com.noveris.chatbubbles.config.ServerConfig;
import com.noveris.chatbubbles.network.BubbleMessagePayload;
import com.noveris.chatbubbles.network.NetworkHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;
import java.util.Locale;

public final class ChatManager {
    private ChatManager() {}

    public static void onServerChat(ServerChatEvent event) {
        String message = event.getRawText() == null ? "" : event.getRawText().trim();
        if (message.isEmpty()) { event.setCanceled(true); return; }
        ServerPlayer sender = event.getPlayer();
        int limit = ServerConfig.MAX_MESSAGE_LENGTH.get();
        if (message.length() > limit) message = message.substring(0, limit);
        if (!ServerConfig.LOCAL_CHAT_ENABLED.get()) return;
        event.setCanceled(true);
        broadcastLocal(sender, message);
    }

    public static void broadcastLocal(ServerPlayer sender, String message) {
        if (!ServerConfig.LOCAL_CHAT_ENABLED.get()) return;
        double radius = ServerConfig.LOCAL_CHAT_RADIUS.get();
        double radiusSquared = radius * radius;
        BubbleMessagePayload payload = new BubbleMessagePayload(sender.getUUID(), sender.getName().getString(), message,
                ServerConfig.BUBBLE_DURATION.get() * 1000L, ServerConfig.MAX_ACTIVE_BUBBLES.get());
        for (ServerPlayer recipient : sender.server.getPlayerList().getPlayers()) {
            if (recipient.level() == sender.level() && recipient.distanceToSqr(sender) <= radiusSquared) {
                NetworkHandler.sendTo(recipient, payload);
            }
        }
    }

    public static void broadcastGlobal(ServerPlayer sender, String raw) {
        if (!ServerConfig.GLOBAL_CHAT_ENABLED.get()) return;
        String message = raw == null ? "" : raw.trim();
        if (message.isEmpty()) return;
        int limit = ServerConfig.MAX_MESSAGE_LENGTH.get();
        if (message.length() > limit) message = message.substring(0, limit);
        String format = ServerConfig.GLOBAL_FORMAT.get().replace("{player}", sender.getName().getString()).replace("{message}", message);
        Component component = Component.literal(format);
        for (ServerPlayer player : sender.server.getPlayerList().getPlayers()) player.sendSystemMessage(component);
    }
}
