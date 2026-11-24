package com.combatparticles.mixin;

import com.combatparticles.ParticleFilter;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

	// Main path: level.addParticle(...) -> ParticleEngine.createParticle(...)
	@Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
	private void combatparticles$filterCreate(ParticleOptions options, double x, double y, double z,
			double xd, double yd, double zd, CallbackInfoReturnable<Particle> cir) {
		if (ParticleFilter.blocksType(options.getType())) {
			cir.setReturnValue(null);
			return;
		}
		ParticleFilter.insideCreate = true;
	}

	@Inject(method = "createParticle", at = @At("RETURN"))
	private void combatparticles$endCreate(ParticleOptions options, double x, double y, double z,
			double xd, double yd, double zd, CallbackInfoReturnable<Particle> cir) {
		ParticleFilter.insideCreate = false;
	}

	// Safety net in case an exception ever left the flag stuck
	@Inject(method = "tick", at = @At("HEAD"))
	private void combatparticles$resetFlag(CallbackInfo ci) {
		ParticleFilter.insideCreate = false;
	}

	// Emitters (this is how the totem pop and crit particles are spawned)
	@Inject(method = "createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;)V",
			at = @At("HEAD"), cancellable = true)
	private void combatparticles$filterEmitter(Entity entity, ParticleOptions options, CallbackInfo ci) {
		if (ParticleFilter.blocksEmitter(entity, options.getType())) {
			ci.cancel();
		}
	}

	@Inject(method = "createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V",
			at = @At("HEAD"), cancellable = true)
	private void combatparticles$filterEmitterTimed(Entity entity, ParticleOptions options, int maxAge, CallbackInfo ci) {
		if (ParticleFilter.blocksEmitter(entity, options.getType())) {
			ci.cancel();
		}
	}

	// Anything added straight to the engine (e.g. block break particles)
	@Inject(method = "add(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
	private void combatparticles$filterDirectAdd(Particle particle, CallbackInfo ci) {
		if (ParticleFilter.blocksDirectAdd(particle)) {
			ci.cancel();
		}
	}
}
