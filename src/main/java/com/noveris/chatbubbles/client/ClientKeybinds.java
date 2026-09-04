package com.noveris.chatbubbles.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.noveris.chatbubbles.NoverisChatBubbles;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.lwjgl.glfw.GLFW;

/** Client-only hotkeys: B toggles bubbles and O opens the mod configuration. */
@EventBusSubscriber(modid = NoverisChatBubbles.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientKeybinds {
    private static final KeyMapping TOGGLE = new KeyMapping("key.noveris_chat_bubbles.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.noveris_chat_bubbles");
    private static final KeyMapping CONFIG = new KeyMapping("key.noveris_chat_bubbles.config", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.categories.noveris_chat_bubbles");

    private ClientKeybinds() {}

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE);
        event.register(CONFIG);
    }

    @EventBusSubscriber(modid = NoverisChatBubbles.MOD_ID, value = Dist.CLIENT)
    public static final class ClientTick {
        private ClientTick() {}
        @SubscribeEvent
        public static void tick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            while (TOGGLE.consumeClick()) {
                boolean enabled = BubbleManager.toggleEnabled();
                if (mc.player != null) mc.player.displayClientMessage(Component.literal("Chat bubbles: " + (enabled ? "ON" : "OFF")), true);
            }
            while (CONFIG.consumeClick()) {
                if (mc.screen == null) {
                    ModList.get().getModContainerById(NoverisChatBubbles.MOD_ID).ifPresent(container ->
                            mc.setScreen(new ConfigurationScreen(container, null)));
                }
            }
        }
    }
}
