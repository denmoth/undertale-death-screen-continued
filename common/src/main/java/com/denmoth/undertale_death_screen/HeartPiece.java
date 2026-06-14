package com.denmoth.undertale_death_screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3x2f;

public class HeartPiece {
    public static final ResourceLocation PIECES_TEXTURE_LOCATION = UndertaleDeathScreenCommon.id("undertale_death/heart_pieces");

    public static final int PIECE_TEXTURE_WIDTH = 40;
    public static final int PIECE_TEXTURE_HEIGHT = 20;

    public static final int PIECE_WIDTH = 5;
    public static final int PIECE_HEIGHT = 5;
    public static final int FRAME_DURATION = 4;
    public static final int TOTAL_FRAMES = PIECE_TEXTURE_WIDTH / PIECE_WIDTH;

    private final boolean animated;
    private final int textureX, textureY;
    private final double angularVelocity;
    public float x, y;
    private double vx, vy;
    private double rotation;
    private int currentFrame = 0;
    private double frameTickAccumulator = 0;

    public HeartPiece(float x, float y, double vx, double vy, int textureX, int textureY, double rotation, double angularVelocity) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.textureX = textureX;
        this.textureY = textureY;
        this.rotation = rotation;
        this.angularVelocity = angularVelocity;

        this.animated = Config.INSTANCE.getStyle() == Config.ShardRenderStyle.ANIMATED;
    }

    public void renderTick(double timeScale) {
        x += (float) (vx * timeScale);
        y += (float) (vy * timeScale);
        vy += 0.1 * timeScale;
        vx *= Math.pow(0.98, timeScale);
        vy *= Math.pow(0.98, timeScale);

        rotation += angularVelocity * timeScale;
        if (rotation >= 360) {
            rotation -= 360;
        } else if (rotation < 0) {
            rotation += 360;
        }

        if (animated) {
            frameTickAccumulator += timeScale;
            while (frameTickAccumulator >= FRAME_DURATION) {
                frameTickAccumulator -= FRAME_DURATION;
                currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            }
        }
    }

    public void render(GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(
                (float) x, (float) y, 0f
        );

        if (!animated) {
            guiGraphics.pose().mulPose(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(rotation)));
            guiGraphics.pose().translate(
                    -PIECE_WIDTH / 2f, -PIECE_HEIGHT / 2f, 0f
            );
        }

        guiGraphics.blit(
                net.minecraft.client.renderer.RenderType::guiTextured,
                PIECES_TEXTURE_LOCATION.withPrefix("textures/gui/sprites/").withSuffix(".png"),
                0,
                0,
                animated ? currentFrame * PIECE_WIDTH : textureX,
                textureY,
                PIECE_WIDTH,
                PIECE_HEIGHT,
                PIECE_TEXTURE_WIDTH,
                PIECE_TEXTURE_HEIGHT
        );

        guiGraphics.pose().popPose();
    }
}
