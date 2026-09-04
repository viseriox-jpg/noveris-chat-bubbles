package com.noveris.chatbubbles.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> TEXT_COLOR, BACKGROUND_COLOR, BORDER_COLOR, FONT;
    public static final ModConfigSpec.DoubleValue BACKGROUND_OPACITY, SCALE;
    public static final ModConfigSpec.IntValue PADDING, MAX_WIDTH, MAX_LINES, RENDER_DISTANCE, DURATION, FADE_IN_DURATION, FADE_OUT_DURATION;
    public static final ModConfigSpec.BooleanValue SHOW_ARROW, SHOW_PLAYER_NAME;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("appearance");
        TEXT_COLOR = b.define("textColor", "#FFFFFF");
        BACKGROUND_COLOR = b.define("backgroundColor", "#101827");
        BORDER_COLOR = b.define("borderColor", "#6EA8FE");
        FONT = b.define("font", "minecraft:default");
        BACKGROUND_OPACITY = b.defineInRange("backgroundOpacity", 0.88D, 0D, 1D);
        SCALE = b.defineInRange("scale", 1.0D, 0.25D, 3D);
        PADDING = b.defineInRange("padding", 4, 0, 16);
        MAX_WIDTH = b.defineInRange("maxWidth", 160, 40, 512);
        MAX_LINES = b.defineInRange("maxLines", 4, 1, 12);
        RENDER_DISTANCE = b.defineInRange("renderDistance", 64, 8, 256);
        DURATION = b.defineInRange("duration", 6, 1, 120);
        FADE_IN_DURATION = b.defineInRange("fadeInDuration", 180, 0, 2000);
        FADE_OUT_DURATION = b.defineInRange("fadeOutDuration", 500, 0, 5000);
        SHOW_ARROW = b.define("showArrow", true);
        SHOW_PLAYER_NAME = b.define("showPlayerName", false);
        b.pop();
        SPEC = b.build();
    }
    private ClientConfig() {}
}
