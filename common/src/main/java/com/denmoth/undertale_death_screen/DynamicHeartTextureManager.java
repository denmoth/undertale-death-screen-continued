package com.denmoth.undertale_death_screen;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/**
 * Generates dynamic textures for the heart shatter animation when a custom
 * heart resource pack is detected.
 *
 * Takes the custom heart sprite pixels and applies the pre-baked displacement
 * data from HeartDisplacementData to produce a heart_shatter-compatible
 * texture and a heart_pieces-compatible texture — at runtime, with no manual
 * asset creation required.
 */
public final class DynamicHeartTextureManager {

    private static final Identifier DYNAMIC_SHATTER_ID =
            UndertaleDeathScreenCommon.id("dynamic/heart_shatter");
    private static final Identifier DYNAMIC_PIECES_ID =
            UndertaleDeathScreenCommon.id("dynamic/heart_pieces");

    // Vanilla texture fallbacks (used when no custom pack is detected)
    private static final Identifier VANILLA_SHATTER =
            UndertaleDeathScreenCommon.id("undertale_death/heart_shatter");
    private static final Identifier VANILLA_PIECES =
            UndertaleDeathScreenCommon.id("undertale_death/heart_pieces");

    private static DynamicTexture dynamicShatter = null;
    private static DynamicTexture dynamicPieces = null;
    private static boolean dynamicRegistered = false;

    // Dimensions matching the static sprite sheets
    private static final int SHATTER_TEX_W = HeartDisplacementData.HEART_WIDTH * HeartDisplacementData.NUM_STAGES;  // 52
    private static final int SHATTER_TEX_H = HeartDisplacementData.HEART_HEIGHT * 4;  // 60 (4 styles)
    private static final int PIECES_TEX_W = HeartDisplacementData.PIECE_WIDTH * HeartDisplacementData.NUM_PIECES;  // 40
    private static final int PIECES_TEX_H = HeartDisplacementData.PIECE_HEIGHT * 4;  // 20 (4 styles)

    private DynamicHeartTextureManager() {}

    /**
     * Returns the ResourceLocation to use for the heart shatter sprite.
     * Returns the dynamic texture if a custom pack is active, otherwise the vanilla fallback.
     */
    public static Identifier getShatterLocation() {
        return dynamicRegistered ? DYNAMIC_SHATTER_ID : VANILLA_SHATTER;
    }

    /**
     * Returns the ResourceLocation to use for the heart pieces sprite.
     */
    public static Identifier getPiecesLocation() {
        return dynamicRegistered ? DYNAMIC_PIECES_ID : VANILLA_PIECES;
    }

    public static boolean isDynamicRegistered() {
        return dynamicRegistered;
    }

    /**
     * Builds dynamic textures from the given custom heart pixels.
     * customHeartPixels is ARGB int[], sized spriteWidth * spriteHeight.
     * The sprite is scaled/mapped to fit the 13x15 heart frame.
     *
     * @param customHeartPixels ARGB pixels of the custom heart sprite (from GUI atlas)
     * @param spriteWidth       width of the custom sprite in pixels
     * @param spriteHeight      height of the custom sprite in pixels
     * @param heartStyle        active style row to write (0=normal,1=poison,2=wither,3=freeze)
     */
    public static void buildTextures(int[] customHeartPixels, int spriteWidth, int spriteHeight, int heartStyle) {
        cleanup();
        try {
            buildShatterTexture(customHeartPixels, spriteWidth, spriteHeight, heartStyle);
            buildPiecesTexture(customHeartPixels, spriteWidth, spriteHeight, heartStyle);
            dynamicRegistered = true;
        } catch (Exception e) {
            UndertaleDeathScreenCommon.logger.error("Failed to build dynamic heart textures: {}", e.getMessage());
            cleanup();
        }
    }

    /** Releases GPU resources. Called when the death screen closes or resources reload. */
    public static void cleanup() {
        dynamicRegistered = false;
        var tm = Minecraft.getInstance().getTextureManager();
        if (dynamicShatter != null) {
            tm.release(DYNAMIC_SHATTER_ID);
            dynamicShatter.close();
            dynamicShatter = null;
        }
        if (dynamicPieces != null) {
            tm.release(DYNAMIC_PIECES_ID);
            dynamicPieces.close();
            dynamicPieces = null;
        }
    }

    // -------------------------------------------------------------------------

    private static void buildShatterTexture(int[] srcPixels, int srcW, int srcH, int targetStyle) {
        NativeImage img = new NativeImage(NativeImage.Format.RGBA, SHATTER_TEX_W, SHATTER_TEX_H, true);

        int hw = HeartDisplacementData.HEART_WIDTH;
        int hh = HeartDisplacementData.HEART_HEIGHT;
        int numStages = HeartDisplacementData.NUM_STAGES;

        // Only write the row that matches the active heart style.
        // The mixin always reads from heartStyle row, so other rows are unused.
        int style = Math.max(0, Math.min(3, targetStyle));
        for (int stage = 0; stage < numStages; stage++) {
            for (int pi = 0; pi < HeartDisplacementData.SHATTER_DISPLACEMENT.length; pi++) {
                int origLx = pi % hw;
                int origLy = pi / hw;
                int enc = HeartDisplacementData.SHATTER_DISPLACEMENT[pi][stage];
                boolean visible = HeartDisplacementData.isVisible(enc);
                if (!visible) continue;

                int dx = HeartDisplacementData.getDx(enc);
                int dy = HeartDisplacementData.getDy(enc);
                int destLx = origLx + dx;
                int destLy = origLy + dy;

                if (destLx < 0 || destLx >= hw || destLy < 0 || destLy >= hh) continue;

                int sampledArgb = sampleHeartPixel(srcPixels, srcW, srcH, origLx, origLy);
                if (((sampledArgb >> 24) & 0xFF) <= 10) continue;

                int destGx = stage * hw + destLx;
                int destGy = style * hh + destLy;
                img.setPixel(destGx, destGy, argbToNativeImage(sampledArgb));
            }
        }

        dynamicShatter = new DynamicTexture(() -> "dynamic_heart_shatter", img);
        Minecraft.getInstance().getTextureManager().register(DYNAMIC_SHATTER_ID, dynamicShatter);
        dynamicShatter.upload();
    }

    private static void buildPiecesTexture(int[] srcPixels, int srcW, int srcH, int targetStyle) {
        NativeImage img = new NativeImage(NativeImage.Format.RGBA, PIECES_TEX_W, PIECES_TEX_H, true);

        int pw = HeartDisplacementData.PIECE_WIDTH;
        int ph = HeartDisplacementData.PIECE_HEIGHT;
        int style = Math.max(0, Math.min(3, targetStyle));

        for (int pieceIdx = 0; pieceIdx < HeartDisplacementData.NUM_PIECES; pieceIdx++) {
            int[] pieceSource = HeartDisplacementData.PIECE_SOURCE[pieceIdx];
            for (int pxi = 0; pxi < pieceSource.length; pxi++) {
                int enc = pieceSource[pxi];
                if (enc == -1) continue;

                int srcX = HeartDisplacementData.getSrcX(enc);
                int srcY = HeartDisplacementData.getSrcY(enc);

                int sampledArgb = sampleHeartPixel(srcPixels, srcW, srcH, srcX, srcY);
                if (((sampledArgb >> 24) & 0xFF) <= 10) continue;

                int plx = pxi % pw;
                int ply = pxi / pw;
                int destGx = pieceIdx * pw + plx;
                int destGy = style * ph + ply;
                img.setPixel(destGx, destGy, argbToNativeImage(sampledArgb));
            }
        }

        dynamicPieces = new DynamicTexture(() -> "dynamic_heart_pieces", img);
        Minecraft.getInstance().getTextureManager().register(DYNAMIC_PIECES_ID, dynamicPieces);
        dynamicPieces.upload();
    }

    /**
     * Maps logical (lx, ly) in the 13x15 Undertale grid to pixels in srcPixels (srcW x srcH).
     * The 9x9 heart sprite maps to [x: 2..10, y: 3..11] inside the 13x15 grid.
     * Pixels outside [2..10, 3..11] return 0 (transparent margin matching heart_shatter.png).
     */
    private static int sampleHeartPixel(int[] srcPixels, int srcW, int srcH, int lx, int ly) {
        int minX = 2;
        int maxX = 10; // inclusive (9px wide)
        int minY = 3;
        int maxY = 11; // inclusive (9px tall)

        if (lx < minX || lx > maxX || ly < minY || ly > maxY) {
            return 0; // transparent padding
        }

        int relX = lx - minX; // 0..8
        int relY = ly - minY; // 0..8
        int gridW = maxX - minX + 1; // 9
        int gridH = maxY - minY + 1; // 9

        int sx = (int) Math.round((relX + 0.5) * srcW / (double) gridW - 0.5);
        int sy = (int) Math.round((relY + 0.5) * srcH / (double) gridH - 0.5);
        sx = Math.max(0, Math.min(srcW - 1, sx));
        sy = Math.max(0, Math.min(srcH - 1, sy));
        return srcPixels[sy * srcW + sx];
    }

    /**
     * NativeImage.setPixel takes ARGB (same format as getPixel returns).
     * No byte swap needed.
     */
    private static int argbToNativeImage(int argb) {
        return argb;
    }
}
