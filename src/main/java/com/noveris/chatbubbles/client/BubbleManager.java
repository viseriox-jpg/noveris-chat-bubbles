package com.noveris.chatbubbles.client;

import com.noveris.chatbubbles.network.BubbleMessagePayload;
import com.noveris.chatbubbles.config.ClientConfig;
import java.util.*;

public final class BubbleManager {
    public record Bubble(String playerName, String text, long expiresAt, long createdAt, long duration) {}
    private static final Map<UUID, Deque<Bubble>> BUBBLES = new HashMap<>();
    private BubbleManager() {}
    public static void receive(BubbleMessagePayload payload) {
        long now = System.currentTimeMillis();
        long duration = Math.min(Math.max(1_000L, payload.serverDurationMillis()), ClientConfig.DURATION.get() * 1000L);
        Deque<Bubble> bubbles = BUBBLES.computeIfAbsent(payload.sender(), ignored -> new ArrayDeque<>());
        cleanup(now);
        int max = Math.max(1, Math.min(10, payload.serverMaxActiveBubbles()));
        while (bubbles.size() >= max) bubbles.removeFirst();
        bubbles.addLast(new Bubble(payload.playerName(), payload.message(), now + duration, now, duration));
    }
    public static Map<UUID, Deque<Bubble>> visible(long now) { cleanup(now); return Collections.unmodifiableMap(BUBBLES); }
    private static void cleanup(long now) { BUBBLES.entrySet().removeIf(e -> { e.getValue().removeIf(b -> b.expiresAt() <= now); return e.getValue().isEmpty(); }); }
    public static void clear() { BUBBLES.clear(); }
}
