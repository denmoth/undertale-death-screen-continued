package com.denmoth.undertale_death_screen.mixin;

import com.denmoth.undertale_death_screen.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;

@Mixin(Screen.class)
public class ConfirmScreenMixin {
    @Inject(method = "extractDeathBackground", at = @At("HEAD"), require = 0, cancellable = true)
    private static void disableDeathTint(GuiGraphicsExtractor guiGraphics, int i, int j, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isDeadOrDying()) {
            if (Config.INSTANCE.getDisableVanillaRedTint() || !Config.INSTANCE.getFadeToVanillaScreen()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void render(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if ((Object) this instanceof ConfirmScreen && Minecraft.getInstance().player != null && Minecraft.getInstance().player.isDeadOrDying()) {
            if (UndertaleDeathScreenCommon.currentBackgroundAlpha > 0.0f) {
                int alpha = (int) (255 * UndertaleDeathScreenCommon.currentBackgroundAlpha);
                int bgColor = (alpha << 24);
                guiGraphics.pose().pushMatrix();
                guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), bgColor);
                guiGraphics.pose().popMatrix();
            }
        }
    }
}
