package com.combatparticles.mixin;

import com.combatparticles.ParticleFilter;
import com.combatparticles.SmallTotem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	// Called when YOUR totem pops (vanilla only uses this for totems). The totem is still used up
	// as normal, only the picture changes: removed, or swapped for the small one.
	@Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
	private void combatparticles$totemAnimation(ItemStack stack, CallbackInfo ci) {
		switch (ParticleFilter.totemMode()) {
			case OFF -> ci.cancel();
			case SMALL -> {
				SmallTotem.start(stack);
				ci.cancel();
			}
			default -> { }
		}
	}
}
