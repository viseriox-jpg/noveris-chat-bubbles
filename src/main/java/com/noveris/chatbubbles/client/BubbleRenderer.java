package com.noveris.chatbubbles.client;

import com.noveris.chatbubbles.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import java.util.*;

@EventBusSubscriber(modid = "noveris_chat_bubbles", value = Dist.CLIENT)
public final class BubbleRenderer {
    private BubbleRenderer() {}
    @SubscribeEvent
    public static void render(RenderPlayerEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.distanceToSqr(player) > Math.pow(ClientConfig.RENDER_DISTANCE.get(), 2)) return;
        Deque<BubbleManager.Bubble> bubbles = BubbleManager.visible(System.currentTimeMillis()).get(player.getUUID());
        if (bubbles == null || bubbles.isEmpty()) return;
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(0, player.getBbHeight() + 0.35, 0);
        pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        // Font coordinates are pixels. Vanilla name tags use -0.025 in world
        // space and a negative XY scale so the text faces the camera correctly.
        float scale = ClientConfig.SCALE.get().floatValue() * 0.025F;
        pose.scale(-scale, -scale, scale);
        Font font = mc.font;
        float y = 0;
        List<BubbleManager.Bubble> ordered = new ArrayList<>(bubbles);
        for (int i = ordered.size() - 1; i >= 0; i--) {
            BubbleManager.Bubble bubble = ordered.get(i);
            String displayText = ClientConfig.SHOW_PLAYER_NAME.get() ? bubble.playerName() + ": " + bubble.text() : bubble.text();
            List<FormattedCharSequence> lines = font.split(Component.literal(displayText), ClientConfig.MAX_WIDTH.get());
            if (lines.size() > ClientConfig.MAX_LINES.get()) lines = lines.subList(0, ClientConfig.MAX_LINES.get());
            int width = 0; for (FormattedCharSequence line : lines) width = Math.max(width, font.width(line));
            int padding = ClientConfig.PADDING.get(); int boxW = width + padding * 2; int boxH = lines.size() * 9 + padding * 2;
            float left = -boxW / 2f;
            float alpha = alpha(bubble, System.currentTimeMillis());
            drawBox(event.getMultiBufferSource(), pose.last().pose(), left, y - boxH, boxW, boxH, color(ClientConfig.BACKGROUND_COLOR.get(), alpha), color(ClientConfig.BORDER_COLOR.get(), alpha));
            int textColor = color(ClientConfig.TEXT_COLOR.get(), alpha);
            for (int line = 0; line < lines.size(); line++) font.drawInBatch(lines.get(line), left + padding, y - boxH + padding + line * 9, textColor, false, pose.last().pose(), event.getMultiBufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            if (ClientConfig.SHOW_ARROW.get()) drawArrow(event.getMultiBufferSource(), pose.last().pose(), 0, y + 1, alpha);
            y -= boxH + 3;
        }
        pose.popPose();
    }
    private static float alpha(BubbleManager.Bubble b, long now) { long in = now - b.createdAt(); long out = b.expiresAt() - now; float a = ClientConfig.FADE_IN_DURATION.get() == 0 ? 1 : Math.min(1f, in / (float) ClientConfig.FADE_IN_DURATION.get()); if (out < ClientConfig.FADE_OUT_DURATION.get()) a = Math.min(a, Math.max(0, out / (float) ClientConfig.FADE_OUT_DURATION.get())); return a; }
    private static int color(String hex, float alpha) { try { int rgb = Integer.parseInt(hex.replace("#", ""), 16) & 0xFFFFFF; return ((int)(alpha * 255) << 24) | rgb; } catch (RuntimeException e) { return ((int)(alpha * 255) << 24) | 0xFFFFFF; } }
    private static void drawBox(MultiBufferSource source, Matrix4f matrix, float x, float y, float w, float h, int background, int border) {
        var buffer = source.getBuffer(RenderType.gui());
        quad(buffer, matrix, x, y, w, h, background);
        float t = 1f;
        quad(buffer, matrix, x, y, w, t, border);
        quad(buffer, matrix, x, y + h - t, w, t, border);
        quad(buffer, matrix, x, y, t, h, border);
        quad(buffer, matrix, x + w - t, y, t, h, border);
    }
    private static void quad(com.mojang.blaze3d.vertex.VertexConsumer buffer, Matrix4f matrix, float x, float y, float w, float h, int color) {
        vertex(buffer, matrix, x, y, color); vertex(buffer, matrix, x + w, y, color); vertex(buffer, matrix, x + w, y + h, color); vertex(buffer, matrix, x, y + h, color);
    }
    private static void drawArrow(MultiBufferSource source, Matrix4f matrix, float center, float y, float alpha) {
        var buffer = source.getBuffer(RenderType.gui());
        int c = color(ClientConfig.BACKGROUND_COLOR.get(), alpha);
        vertex(buffer, matrix, center - 4, y, c); vertex(buffer, matrix, center + 4, y, c);
        vertex(buffer, matrix, center, y + 5, c); vertex(buffer, matrix, center - 4, y, c);
    }
    private static void vertex(com.mojang.blaze3d.vertex.VertexConsumer v, Matrix4f m, float x, float y, int c) { v.addVertex(m, x, y, 0).setColor(c); }
}
