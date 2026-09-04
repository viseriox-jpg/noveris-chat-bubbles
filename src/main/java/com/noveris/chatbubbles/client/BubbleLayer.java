package com.noveris.chatbubbles.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.noveris.chatbubbles.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import com.noveris.chatbubbles.NoverisChatBubbles;

/** A real player render layer; this is invoked as part of every player renderer. */
public final class BubbleLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static long lastTrace;
    public BubbleLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || player.isInvisible() || mc.player.distanceToSqr(player) > Math.pow(ClientConfig.RENDER_DISTANCE.get(), 2)) return;
        Deque<BubbleManager.Bubble> bubbles = BubbleManager.visible(System.currentTimeMillis()).get(player.getUUID());
        if (!BubbleManager.isEnabled() || bubbles == null || bubbles.isEmpty()) return;
        long traceNow = System.currentTimeMillis();
        if (traceNow - lastTrace > 1000) {
            lastTrace = traceNow;
            NoverisChatBubbles.LOGGER.info("Bubble render layer invoked for {} with {} bubble(s)", player.getUUID(), bubbles.size());
        }

        pose.pushPose();
        pose.translate(0.0D, player.getBbHeight() + 0.35D, 0.0D);
        pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        float scale = ClientConfig.SCALE.get().floatValue() * 0.025F;
        pose.scale(-scale, -scale, scale);

        Font font = mc.font;
        float y = 0.0F;
        List<BubbleManager.Bubble> ordered = new ArrayList<>(bubbles);
        long now = System.currentTimeMillis();
        for (int i = ordered.size() - 1; i >= 0; i--) {
            BubbleManager.Bubble bubble = ordered.get(i);
            Component display = ClientConfig.SHOW_PLAYER_NAME.get()
                    ? Component.empty().append(bubble.playerName()).append(Component.literal(": ")).append(Component.literal(bubble.text()))
                    : Component.literal(bubble.text());
            List<FormattedCharSequence> lines = font.split(display, ClientConfig.MAX_WIDTH.get());
            if (lines.size() > ClientConfig.MAX_LINES.get()) lines = lines.subList(0, ClientConfig.MAX_LINES.get());
            int width = 0;
            for (FormattedCharSequence line : lines) width = Math.max(width, font.width(line));
            int padding = ClientConfig.PADDING.get();
            int boxW = Math.max(1, width + padding * 2);
            int boxH = Math.max(9, lines.size() * 9 + padding * 2);
            float left = -boxW / 2.0F;
            float alpha = alpha(bubble, now);
            drawBox(source, pose.last().pose(), left, y - boxH, boxW, boxH, color(ClientConfig.BACKGROUND_COLOR.get(), alpha), color(ClientConfig.BORDER_COLOR.get(), alpha));
            int textColor = color(ClientConfig.TEXT_COLOR.get(), alpha);
            for (int line = 0; line < lines.size(); line++) {
                font.drawInBatch(lines.get(line), left + padding, y - boxH + padding + line * 9,
                        textColor, false, pose.last().pose(), source, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
            }
            if (ClientConfig.SHOW_ARROW.get()) drawArrow(source, pose.last().pose(), 0, y + 1, alpha);
            y -= boxH + 3;
        }
        pose.popPose();
    }

    private static float alpha(BubbleManager.Bubble b, long now) {
        long in = now - b.createdAt();
        long out = b.expiresAt() - now;
        float value = ClientConfig.FADE_IN_DURATION.get() == 0 ? 1.0F : Math.min(1.0F, in / (float) ClientConfig.FADE_IN_DURATION.get());
        if (out < ClientConfig.FADE_OUT_DURATION.get()) value = Math.min(value, Math.max(0.0F, out / (float) ClientConfig.FADE_OUT_DURATION.get()));
        return value;
    }

    private static int color(String hex, float alpha) {
        try { return ((int) (alpha * 255.0F) << 24) | (Integer.parseInt(hex.replace("#", ""), 16) & 0xFFFFFF); }
        catch (RuntimeException ignored) { return ((int) (alpha * 255.0F) << 24) | 0xFFFFFF; }
    }

    private static void drawBox(MultiBufferSource source, org.joml.Matrix4f matrix, float x, float y, float w, float h, int background, int border) {
        var buffer = source.getBuffer(RenderType.gui());
        quad(buffer, matrix, x, y, w, h, background);
        quad(buffer, matrix, x, y, w, 1, border); quad(buffer, matrix, x, y + h - 1, w, 1, border);
        quad(buffer, matrix, x, y, 1, h, border); quad(buffer, matrix, x + w - 1, y, 1, h, border);
    }
    private static void drawArrow(MultiBufferSource source, org.joml.Matrix4f matrix, float center, float y, float alpha) {
        var buffer = source.getBuffer(RenderType.gui()); int c = color(ClientConfig.BACKGROUND_COLOR.get(), alpha);
        vertex(buffer, matrix, center - 4, y, c); vertex(buffer, matrix, center + 4, y, c); vertex(buffer, matrix, center, y + 5, c); vertex(buffer, matrix, center - 4, y, c);
    }
    private static void quad(com.mojang.blaze3d.vertex.VertexConsumer b, org.joml.Matrix4f m, float x, float y, float w, float h, int c) {
        vertex(b, m, x, y, c); vertex(b, m, x + w, y, c); vertex(b, m, x + w, y + h, c); vertex(b, m, x, y + h, c);
    }
    private static void vertex(com.mojang.blaze3d.vertex.VertexConsumer b, org.joml.Matrix4f m, float x, float y, int c) { b.addVertex(m, x, y, 0).setColor(c); }
}
