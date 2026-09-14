package com.denmoth.undertale_death_screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.denmoth.undertale_death_screen.mixin.SpriteContentsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * Detects whether a resource pack has replaced the vanilla heart texture,
 * and provides raw pixel data for the custom heart sprite.
 *
 * Reads the correct sprite based on the active heart style (normal/poison/wither/freeze)
 * and compares against the exact known vanilla palette for that style.
 */
public final class CustomHeartDetector {

    // Sprite names per style (style index matches COLUMN_SELECTOR in DeathScreenMixin)
    private static final Identifier[] NORMAL_SPRITES = {
        Identifier.withDefaultNamespace("hud/heart/full"),          // 0: normal
        Identifier.withDefaultNamespace("hud/heart/poisoned_full"), // 1: poison
        Identifier.withDefaultNamespace("hud/heart/withered_full"), // 2: wither
        Identifier.withDefaultNamespace("hud/heart/frozen_full"),   // 3: freeze
    };
    private static final Identifier[] HARDCORE_SPRITES = {
        Identifier.withDefaultNamespace("hud/heart/hardcore_full"),          // 0: normal
        Identifier.withDefaultNamespace("hud/heart/poisoned_hardcore_full"), // 1: poison
        Identifier.withDefaultNamespace("hud/heart/withered_hardcore_full"), // 2: wither
        Identifier.withDefaultNamespace("hud/heart/frozen_hardcore_full"),   // 3: freeze
    };

    // Exact vanilla palettes per style — extracted from heart_shatter.png and heart_shatter_hardcore.png
    // Tolerance applied per-channel during comparison.
    private static final int[][] VANILLA_PALETTES = {
        // Style 0: normal
        { 0xFF000000, 0xFFBB1313, 0xFFFF1313, 0xFFFFC8C8 },
        // Style 1: poison
        { 0xFF000000, 0xFF685308, 0xFF8B8712, 0xFF947818, 0xFFAC7BA2 },
        // Style 2: wither
        { 0xFF000000, 0xFF1D1D1D, 0xFF202020, 0xFF272727, 0xFF2A0E0E,
          0xFF2B2B2B, 0xFF2F0F0F, 0xFF391C1C, 0xFF3B1313, 0xFF471C1C, 0xFFCBCBCB },
        // Style 3: freeze
        { 0xFF000000, 0xFF010202, 0xFF01BEF2, 0xFF4CBAD8, 0xFF80E5EF,
          0xFFA7F6FE, 0xFFA8F7FF, 0xFFE1FCFF },
    };
    private static final int[][] VANILLA_PALETTES_HC = {
        // Style 0: hardcore normal
        { 0xFF000000, 0xFF600707, 0xFFBB1313, 0xFFFF1313 },
        // Style 1: hardcore poison
        { 0xFF000000, 0xFF605753, 0xFF685308, 0xFF8B8712, 0xFF947818 },
        // Style 2: hardcore wither
        { 0xFF000000, 0xFF202020, 0xFF2A0E0E, 0xFF2B2B2B, 0xFF2C0E0E,
          0xFF2F0F0F, 0xFF3B1313, 0xFF9C9C9C, 0xFF9D9D9D, 0xFFB4B4B4, 0xFFB5B5B5, 0xFFCBCBCB },
        // Style 3: hardcore freeze
        { 0xFF000000, 0xFF010202, 0xFF01BEF2, 0xFF4CBAD8, 0xFF67848C,
          0xFF6B949E, 0xFF80E5EF, 0xFFA8F7FF, 0xFFE1FCFF },
    };

    private static final int PALETTE_TOLERANCE = 8; // per-channel max delta

    // Cached results; null = not yet evaluated
    private static Boolean cachedIsCustom = null;
    private static int[] cachedPixels = null;
    private static int cachedSpriteWidth = 0;
    private static int cachedSpriteHeight = 0;
    private static int cachedForStyle = -1;
    private static boolean cachedForHardcore = false;

    private CustomHeartDetector() {}

    /** Invalidate cache when resource packs reload. */
    public static void invalidate() {
        cachedIsCustom = null;
        cachedPixels = null;
        cachedSpriteWidth = 0;
        cachedSpriteHeight = 0;
        cachedForStyle = -1;
        cachedForHardcore = false;
    }

    /**
     * Returns true if a non-vanilla heart texture is active for the given style.
     * @param heartStyle 0=normal, 1=poison, 2=wither, 3=freeze
     * @param hardcore   whether the player is in hardcore mode
     */
    public static boolean hasCustomHeart(int heartStyle, boolean hardcore) {
        if (cachedIsCustom == null || cachedForStyle != heartStyle || cachedForHardcore != hardcore) {
            detect(heartStyle, hardcore);
        }
        return Boolean.TRUE.equals(cachedIsCustom);
    }

    /** ARGB pixel array of the custom heart sprite, or null if vanilla. */
    public static int[] getCustomHeartPixels() { return cachedPixels; }
    public static int getSpriteWidth()          { return cachedSpriteWidth; }
    public static int getSpriteHeight()         { return cachedSpriteHeight; }

    // -------------------------------------------------------------------------

    private static void detect(int heartStyle, boolean hardcore) {
        cachedForStyle = heartStyle;
        cachedForHardcore = hardcore;
        try {
            int styleIdx = Math.max(0, Math.min(3, heartStyle));
            Identifier spriteId = hardcore ? HARDCORE_SPRITES[styleIdx] : NORMAL_SPRITES[styleIdx];
            int[] vanillaPalette = hardcore ? VANILLA_PALETTES_HC[styleIdx] : VANILLA_PALETTES[styleIdx];

            int[] pixels = readSpritePixels(spriteId);
            if (pixels == null) {
                cachedIsCustom = false;
                return;
            }

            if (isVanillaColors(pixels, vanillaPalette)) {
                cachedIsCustom = false;
                cachedPixels = null;
            } else {
                cachedIsCustom = true;
                cachedPixels = pixels;
                UndertaleDeathScreenCommon.logger.info(
                    "Custom heart texture detected for style={} hardcore={} — generating dynamic textures",
                    styleIdx, hardcore);
            }
        } catch (Exception e) {
            UndertaleDeathScreenCommon.logger.warn("Failed to detect custom heart texture: {}", e.getMessage());
            cachedIsCustom = false;
        }
    }

    private static int[] readSpritePixels(Identifier textureName) {
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        SpriteId spriteId = new SpriteId(AtlasIds.GUI, textureName);
        TextureAtlasSprite sprite = atlasManager.get(spriteId);
        if (sprite == null) return null;

        var contents = sprite.contents();
        int sw = contents.width();
        int sh = contents.height();
        NativeImage originalImage = ((SpriteContentsAccessor) (Object) contents).undertale_death_animation$getOriginalImage();
        if (originalImage == null) return null;

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
        cachedSpriteWidth = sw;
        cachedSpriteHeight = sh;
        return pixels;
    }

    private static boolean isVanillaColors(int[] pixels, int[] palette) {
        for (int px : pixels) {
            int a = (px >> 24) & 0xFF;
            if (a <= 10) continue;
            if (!matchesAny(px, palette)) return false;
        }
        return true;
    }

    private static boolean matchesAny(int argb, int[] palette) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        for (int vc : palette) {
            if (Math.abs(r - ((vc >> 16) & 0xFF)) <= PALETTE_TOLERANCE
                    && Math.abs(g - ((vc >> 8) & 0xFF)) <= PALETTE_TOLERANCE
                    && Math.abs(b - (vc & 0xFF)) <= PALETTE_TOLERANCE) {
                return true;
            }
        }
        return false;
    }
}
