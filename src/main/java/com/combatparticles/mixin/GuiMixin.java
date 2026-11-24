package com.combatparticles.mixin;

import com.combatparticles.SmallTotem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

	// Draws the small totem on top of the HUD, after everything else
	@Inject(method = "render", at = @At("TAIL"))
	private void combatparticles$drawSmallTotem(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		SmallTotem.render(graphics);
	}
}
