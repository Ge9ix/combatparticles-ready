package com.combatparticles;

import net.fabricmc.api.ClientModInitializer;

public class CombatParticlesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CombatConfig.load();
		ParticleFilter.rebuild();
	}
}
