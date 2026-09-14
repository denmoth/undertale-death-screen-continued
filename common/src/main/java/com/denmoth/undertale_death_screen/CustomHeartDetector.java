package com.denmoth.undertale_death_screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.denmoth.undertale_death_screen.mixin.SpriteContentsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * Detects whether a resource pack has replaced the vanilla heart texture,
 * and provides raw pixel data for the custom heart sprite.
 *
 * Reads the correct sprite based on the active heart style (normal/poison/wither/freeze)
 * and compares against the exact known vanilla palette for that style.
 *
 * NativeImage pixel format note:
 *   getPixel(x,y) returns raw memory int — on little-endian x86 with RGBA layout
 *   this is 0xAABBGGRR (i.e. ABGR as a Java int), same as getPixelABGR.
 *   We always read/write using ABGR convention to match what NativeImage expects.
 */
public final class CustomHeartDetector {

    private static final Identifier[] NORMAL_SPRITES = {
        Identifier.withDefaultNamespace("hud/heart/full"),
        Identifier.withDefaultNamespace("hud/heart/poisoned_full"),
        Identifier.withDefaultNamespace("hud/heart/withered_full"),
        Identifier.withDefaultNamespace("hud/heart/frozen_full"),
    };
    private static final Identifier[] HARDCORE_SPRITES = {
        Identifier.withDefaultNamespace("hud/heart/hardcore_full"),
        Identifier.withDefaultNamespace("hud/heart/poisoned_hardcore_full"),
        Identifier.withDefaultNamespace("hud/heart/withered_hardcore_full"),
        Identifier.withDefaultNamespace("hud/heart/frozen_hardcore_full"),
    };

    // Palettes stored as ARGB (Java int convention A=bits31-24, R=23-16, G=15-8, B=7-0)
    // Extracted from our heart_shatter.png sprite sheet rows.
    private static final int[][] VANILLA_PALETTES = {
        // Style 0: normal red
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

    private static final int PALETTE_TOLERANCE = 8;

    private static Boolean cachedIsCustom = null;
    private static int[] cachedPixels = null;
    private static int cachedSpriteWidth = 0;
    private static int cachedSpriteHeight = 0;
    private static int cachedForStyle = -1;
    private static boolean cachedForHardcore = false;

    private CustomHeartDetector() {}

    public static void invalidate() {
        cachedIsCustom = null;
        cachedPixels = null;
        cachedSpriteWidth = 0;
        cachedSpriteHeight = 0;
        cachedForStyle = -1;
        cachedForHardcore = false;
    }

    public static boolean hasCustomHeart(int heartStyle, boolean hardcore) {
        if (cachedIsCustom == null || cachedForStyle != heartStyle || cachedForHardcore != hardcore) {
            detect(heartStyle, hardcore);
        }
        return Boolean.TRUE.equals(cachedIsCustom);
    }

    public static int[] getCustomHeartPixels() { return cachedPixels; }
    public static int getSpriteWidth()          { return cachedSpriteWidth; }
    public static int getSpriteHeight()         { return cachedSpriteHeight; }

    // -------------------------------------------------------------------------

    private static void detect(int heartStyle, boolean hardcore) {
        cachedForStyle = heartStyle;
        cachedForHardcore = hardcore;
        boolean debug = Config.INSTANCE.getDebugMode();

        try {
            int styleIdx = Math.max(0, Math.min(3, heartStyle));
            Identifier spriteName = hardcore ? HARDCORE_SPRITES[styleIdx] : NORMAL_SPRITES[styleIdx];
            int[] vanillaPalette = hardcore ? VANILLA_PALETTES_HC[styleIdx] : VANILLA_PALETTES[styleIdx];

            if (debug) {
                UndertaleDeathScreenCommon.logger.info(
                    "[UDSC DEBUG] detecting heart: style={} hardcore={} sprite={}",
                    styleIdx, hardcore, spriteName);
            }

            // Read pixel data from GUI atlas
            int[] pixels = readSpritePixels(spriteName, debug);
            if (pixels == null) {
                UndertaleDeathScreenCommon.logger.warn("[UDSC] Could not read sprite pixels for {}", spriteName);
                cachedIsCustom = false;
                return;
            }

            // Find the first non-matching pixel for debug output
            int firstMismatchIdx = -1;
            int firstMismatchColor = 0;
            for (int i = 0; i < pixels.length; i++) {
                int a = (pixels[i] >> 24) & 0xFF;
                if (a <= 10) continue;
                if (!matchesAny(pixels[i], vanillaPalette)) {
                    firstMismatchIdx = i;
                    firstMismatchColor = pixels[i];
                    break;
                }
            }

            boolean isVanilla = (firstMismatchIdx == -1);

            if (debug) {
                UndertaleDeathScreenCommon.logger.info(
                    "[UDSC DEBUG] sprite={}x{} totalPixels={} isVanilla={}",
                    cachedSpriteWidth, cachedSpriteHeight, pixels.length, isVanilla);
                if (!isVanilla) {
                    int r = (firstMismatchColor >> 16) & 0xFF;
                    int g = (firstMismatchColor >> 8) & 0xFF;
                    int b = firstMismatchColor & 0xFF;
                    int a = (firstMismatchColor >> 24) & 0xFF;
                    UndertaleDeathScreenCommon.logger.info(
                        "[UDSC DEBUG] first mismatch at pixel #{}: ARGB=0x{} rgb({},{},{}) a={}",
                        firstMismatchIdx, Integer.toHexString(firstMismatchColor).toUpperCase(), r, g, b, a);
                    UndertaleDeathScreenCommon.logger.info("[UDSC DEBUG] vanilla palette for style {}:", styleIdx);
                    for (int vc : vanillaPalette) {
                        UndertaleDeathScreenCommon.logger.info("  0x{} rgb({},{},{})",
                            Integer.toHexString(vc).toUpperCase(),
                            (vc >> 16) & 0xFF, (vc >> 8) & 0xFF, vc & 0xFF);
                    }
                    // Print ALL unique opaque pixel colors for diagnosis
                    java.util.Set<Integer> unique = new java.util.LinkedHashSet<>();
                    for (int px : pixels) {
                        if (((px >> 24) & 0xFF) > 10) unique.add(px);
                    }
                    UndertaleDeathScreenCommon.logger.info("[UDSC DEBUG] all unique opaque colors in sprite ({}):", unique.size());
                    for (int vc : unique) {
                        UndertaleDeathScreenCommon.logger.info("  0x{}  rgb({},{},{})",
                            Integer.toHexString(vc).toUpperCase(),
                            (vc >> 16) & 0xFF, (vc >> 8) & 0xFF, vc & 0xFF);
                    }
                }
            }

            if (isVanilla) {
                if (debug) UndertaleDeathScreenCommon.logger.info("[UDSC DEBUG] → vanilla, no dynamic textures");
                cachedIsCustom = false;
                cachedPixels = null;
            } else {
                UndertaleDeathScreenCommon.logger.info(
                    "[UDSC] Custom heart detected (style={} hardcore={}) — building dynamic textures",
                    styleIdx, hardcore);
                cachedIsCustom = true;
                cachedPixels = pixels;
            }

        } catch (Exception e) {
            UndertaleDeathScreenCommon.logger.warn("[UDSC] Failed to detect custom heart: {}", e.getMessage());
            if (Config.INSTANCE.getDebugMode()) {
                UndertaleDeathScreenCommon.logger.warn("[UDSC DEBUG] stacktrace:", e);
            }
            cachedIsCustom = false;
        }
    }

    /**
     * Reads pixels from the named sprite in the GUI atlas.
     * Returns ARGB int[] (A=bits31-24, R=23-16, G=15-8, B=7-0), or null on failure.
     *
     * NativeImage.getPixel() returns raw memory as int.
     * For RGBA images on x86 little-endian the in-memory layout is R,G,B,A per byte,
     * which as a little-endian int reads as 0xAABBGGRR — i.e. ABGR.
     * So bits 0-7=R, 8-15=G, 16-23=B, 24-31=A.
     */
    private static int[] readSpritePixels(Identifier spriteName, boolean debug) {
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        TextureAtlas guiAtlas = atlasManager.getAtlasOrThrow(AtlasIds.GUI);
        TextureAtlasSprite sprite = guiAtlas.getSprite(spriteName);

        if (sprite == null) {
            if (debug) UndertaleDeathScreenCommon.logger.info("[UDSC DEBUG] getSprite returned null for {}", spriteName);
            return null;
        }

        var contents = sprite.contents();

        // Reject missing texture (pink/black checkerboard)
        if (contents.name().equals(MissingTextureAtlasSprite.getLocation())) {
            if (debug) UndertaleDeathScreenCommon.logger.info("[UDSC DEBUG] sprite is missing texture for {}", spriteName);
            return null;
        }

        int sw = contents.width();
        int sh = contents.height();

        NativeImage originalImage = ((SpriteContentsAccessor) (Object) contents).undertale_death_animation$getOriginalImage();
        if (originalImage == null) {
            if (debug) UndertaleDeathScreenCommon.logger.info("[UDSC DEBUG] originalImage is null for {}", spriteName);
            return null;
        }

        if (debug) {
            UndertaleDeathScreenCommon.logger.info(
                "[UDSC DEBUG] sprite={} size={}x{} format={}",
                spriteName, sw, sh, originalImage.format());
        }

        int[] pixels = new int[sw * sh];
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                // getPixel returns ABGR (bits: A=31-24, B=23-16, G=15-8, R=7-0)
                // Convert to ARGB (A=31-24, R=23-16, G=15-8, B=7-0)
                int abgr = originalImage.getPixel(x, y);
                int a = (abgr >> 24) & 0xFF;
                int b = (abgr >> 16) & 0xFF;
                int g = (abgr >>  8) & 0xFF;
                int r =  abgr        & 0xFF;
                pixels[y * sw + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        cachedSpriteWidth = sw;
        cachedSpriteHeight = sh;
        return pixels;
    }

    private static boolean matchesAny(int argb, int[] palette) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >>  8) & 0xFF;
        int b =  argb        & 0xFF;
        for (int vc : palette) {
            if (Math.abs(r - ((vc >> 16) & 0xFF)) <= PALETTE_TOLERANCE
                    && Math.abs(g - ((vc >>  8) & 0xFF)) <= PALETTE_TOLERANCE
                    && Math.abs(b - ( vc         & 0xFF)) <= PALETTE_TOLERANCE) {
                return true;
            }
        }
        return false;
    }
}
