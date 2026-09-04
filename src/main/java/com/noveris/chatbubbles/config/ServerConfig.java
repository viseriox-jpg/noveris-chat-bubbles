package com.noveris.chatbubbles.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue LOCAL_CHAT_RADIUS;
    public static final ModConfigSpec.IntValue MAX_MESSAGE_LENGTH;
    public static final ModConfigSpec.IntValue BUBBLE_DURATION;
    public static final ModConfigSpec.IntValue MAX_ACTIVE_BUBBLES;
    public static final ModConfigSpec.BooleanValue GLOBAL_CHAT_ENABLED;
    public static final ModConfigSpec.BooleanValue LOCAL_CHAT_ENABLED;
    public static final ModConfigSpec.ConfigValue<String> GLOBAL_FORMAT;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("chat");
        LOCAL_CHAT_RADIUS = b.comment("Maximum three-dimensional distance for local chat.").defineInRange("localChatRadius", 18, 1, 128);
        MAX_MESSAGE_LENGTH = b.comment("Maximum UTF-16 characters accepted from a player.").defineInRange("maxMessageLength", 256, 1, 2048);
        BUBBLE_DURATION = b.comment("Bubble duration in seconds; clients cannot extend this value.").defineInRange("bubbleDuration", 6, 1, 120);
        MAX_ACTIVE_BUBBLES = b.comment("Maximum active bubbles kept for each player.").defineInRange("maxActiveBubbles", 3, 1, 10);
        GLOBAL_CHAT_ENABLED = b.define("globalChatEnabled", true);
        LOCAL_CHAT_ENABLED = b.define("localChatEnabled", true);
        GLOBAL_FORMAT = b.comment("Placeholders: {player}, {message}").define("globalFormat", "[Global] {player}: {message}");
        b.pop();
        SPEC = b.build();
    }
    private ServerConfig() {}
}
