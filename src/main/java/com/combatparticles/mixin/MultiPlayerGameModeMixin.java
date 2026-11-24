package com.combatparticles.mixin;

import com.combatparticles.ParticleFilter;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

	// Remembers what YOU just hit, so "only my crits" can tell your crit particles from other players'
	@Inject(method = "attack", at = @At("HEAD"))
	private void combatparticles$noteAttack(Player player, Entity target, CallbackInfo ci) {
		ParticleFilter.noteAttack(target);
	}
}
