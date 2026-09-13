package com.denmoth.undertale_death_screen;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * Detects whether a resource pack has replaced the vanilla heart texture,
 * and provides raw pixel data for the custom heart sprite.
 */
public final class CustomHeartDetector {

    // Heart sprite in the GUI atlas
    private static final Identifier HEART_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/heart/full");

    // Cached results; null = not yet evaluated
    private static Boolean cachedIsCustom = null;
    private static int[] cachedPixels = null;
    private static int cachedSpriteWidth = 0;
    private static int cachedSpriteHeight = 0;

    private CustomHeartDetector() {}

    /** Invalidate cache when resource packs reload. */
    public static void invalidate() {
        cachedIsCustom = null;
        cachedPixels = null;
        cachedSpriteWidth = 0;
        cachedSpriteHeight = 0;
    }

    /** Returns true if a non-vanilla heart texture is active. */
    public static boolean hasCustomHeart() {
        if (cachedIsCustom == null) detect();
        return Boolean.TRUE.equals(cachedIsCustom);
    }

    /** ARGB pixel array of the custom heart sprite, or null if vanilla. */
    public static int[] getCustomHeartPixels() {
        if (cachedIsCustom == null) detect();
        return cachedPixels;
    }

    public static int getSpriteWidth() { return cachedSpriteWidth; }
    public static int getSpriteHeight() { return cachedSpriteHeight; }

    // -------------------------------------------------------------------------

    private static void detect() {
        try {
            AtlasManager atlasManager = Minecraft.getInstance().atlasManager;
            SpriteId spriteId = new SpriteId(AtlasIds.GUI, HEART_FULL_TEXTURE);
            TextureAtlasSprite sprite = atlasManager.get(spriteId);

            if (sprite == null) {
                UndertaleDeathScreenCommon.logger.warn("Could not find heart sprite in GUI atlas");
                cachedIsCustom = false;
                return;
            }

            var contents = sprite.contents();
            int sw = contents.width();
            int sh = contents.height();

            // Access the originalImage field via access widener
            NativeImage originalImage = contents.originalImage;
            if (originalImage == null) {
                cachedIsCustom = false;
                return;
            }

            int[] pixels = new int[sw * sh];
            for (int y = 0; y < sh; y++) {
                for (int x = 0; x < sw; x++) {
                    // NativeImage.getPixel returns ABGR; convert to ARGB
                    int abgr = originalImage.getPixel(x, y);
                    int a = (abgr >> 24) & 0xFF;
                    int b = (abgr >> 16) & 0xFF;
                    int g = (abgr >> 8) & 0xFF;
                    int r = abgr & 0xFF;
                    pixels[y * sw + x] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            if (isVanillaColors(pixels)) {
                cachedIsCustom = false;
                cachedPixels = null;
            } else {
                cachedIsCustom = true;
                cachedPixels = pixels;
                cachedSpriteWidth = sw;
                cachedSpriteHeight = sh;
                UndertaleDeathScreenCommon.logger.info(
                    "Custom heart texture detected ({}x{}) — generating dynamic heart textures", sw, sh);
            }
        } catch (Exception e) {
            UndertaleDeathScreenCommon.logger.warn("Failed to detect custom heart texture: {}", e.getMessage());
            cachedIsCustom = false;
        }
    }

    /**
     * Returns true if all opaque pixels exactly match the vanilla heart palette
     * (within a small tolerance for any platform color conversion rounding).
     *
     * Vanilla heart uses exactly 4 colors:
     *   0xFF000000 - black outline/shadow
     *   0xFFBB1313 - dark red
     *   0xFFFF1313 - bright red
     *   0xFFFFC8C8 - light pink highlight
     */
    private static final int[] VANILLA_PALETTE = {
        0xFF000000,
        0xFFBB1313,
        0xFFFF1313,
        0xFFFFC8C8,
    };
    private static final int PALETTE_TOLERANCE = 8; // per-channel max delta

    private static boolean isVanillaColors(int[] pixels) {
        for (int px : pixels) {
            int a = (px >> 24) & 0xFF;
            if (a <= 10) continue;
            if (!matchesAnyVanillaColor(px)) return false;
        }
        return true;
    }

    private static boolean matchesAnyVanillaColor(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        for (int vc : VANILLA_PALETTE) {
            int vr = (vc >> 16) & 0xFF;
            int vg = (vc >> 8) & 0xFF;
            int vb = vc & 0xFF;
            if (Math.abs(r - vr) <= PALETTE_TOLERANCE
                    && Math.abs(g - vg) <= PALETTE_TOLERANCE
                    && Math.abs(b - vb) <= PALETTE_TOLERANCE) {
                return true;
            }
        }
        return false;
    }
}
