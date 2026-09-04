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
        // PlayerEvent.NameFormat (used by noveris-races /apelido) is reflected by
        // ServerPlayer#getDisplayName after refreshDisplayName(). This keeps the
        // integration optional and preserves the real UUID as the identity.
        Component displayName = sender.getDisplayName();
        BubbleMessagePayload payload = new BubbleMessagePayload(sender.getUUID(), displayName, message,
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
        Component component = formatGlobal(sender.getDisplayName(), message, ServerConfig.GLOBAL_FORMAT.get());
        for (ServerPlayer player : sender.server.getPlayerList().getPlayers()) player.sendSystemMessage(component);
    }

    /** Builds the configured format without flattening the formatted nickname. */
    private static Component formatGlobal(Component displayName, String message, String format) {
        Component result = Component.empty();
        int cursor = 0;
        while (cursor < format.length()) {
            int playerToken = format.indexOf("{player}", cursor);
            int messageToken = format.indexOf("{message}", cursor);
            int next = -1;
            String token = null;
            if (playerToken >= 0 && (messageToken < 0 || playerToken < messageToken)) { next = playerToken; token = "{player}"; }
            else if (messageToken >= 0) { next = messageToken; token = "{message}"; }
            if (next < 0) { result = result.append(Component.literal(format.substring(cursor))); break; }
            if (next > cursor) result = result.append(Component.literal(format.substring(cursor, next)));
            result = result.append(token.equals("{player}") ? displayName : Component.literal(message));
            cursor = next + token.length();
        }
        return result;
    }
}
