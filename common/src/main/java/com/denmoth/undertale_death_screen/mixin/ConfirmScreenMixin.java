package com.denmoth.undertale_death_screen.mixin;

import com.denmoth.undertale_death_screen.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.denmoth.undertale_death_screen.UndertaleDeathScreenCommon;

@Mixin(Screen.class)
public class ConfirmScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
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
