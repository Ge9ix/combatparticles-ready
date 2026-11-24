package com.combatparticles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All the decisions. The mixins just ask this class "should I block this?".
 * Kept cheap on purpose: particles are spawned a lot, and this runs for every one.
 */
public final class ParticleFilter {
	/** How long after you hit something its crit particles still count as yours (covers ping). */
	private static final long ATTACK_WINDOW_MS = 600;

	/** true only while ParticleEngine.createParticle is running (that path is already filtered by type). */
	public static boolean insideCreate = false;

	private static boolean active = false;
	private static TotemAnimation totemAnimation = TotemAnimation.VANILLA;
	private static boolean critOnlyMine = false;
	private static final boolean[] ALLOWED = new boolean[ParticleCategory.values().length];
	private static final Map<ParticleType<?>, ParticleCategory> TYPE_CACHE = new ConcurrentHashMap<>();

	// the last thing YOU attacked
	private static int lastAttackedId = Integer.MIN_VALUE;
	private static long lastAttackMs = 0;

	private ParticleFilter() {}

	/** Re-reads the config. Call after load and after every settings change. */
	public static void rebuild() {
		CombatConfig config = CombatConfig.get();
		active = config.enabled;
		ProfileSettings settings = config.activeSettings();
		for (ParticleCategory category : ParticleCategory.values()) {
			ALLOWED[category.ordinal()] = settings.allows(category);
		}
		totemAnimation = settings.totemAnimation;
		critOnlyMine = settings.critOnlyMine;
	}

	/** What to do when your own totem pops. Vanilla whenever the mod is switched off. */
	public static TotemAnimation totemMode() {
		return active ? totemAnimation : TotemAnimation.VANILLA;
	}

	/** Remember that you just attacked this entity (called from MultiPlayerGameMode.attack). */
	public static void noteAttack(Entity target) {
		if (target != null) {
			lastAttackedId = target.getId();
			lastAttackMs = System.currentTimeMillis();
		}
	}

	private static boolean isMyRecentTarget(Entity entity) {
		return entity != null
			&& entity.getId() == lastAttackedId
			&& System.currentTimeMillis() - lastAttackMs <= ATTACK_WINDOW_MS;
	}

	/** Particles created through the normal path (level.addParticle). */
	public static boolean blocksType(ParticleType<?> type) {
		if (!active) {
			return false;
		}
		return !ALLOWED[categoryOf(type).ordinal()];
	}

	/**
	 * Tracking emitters (this is how totem pops and crits are spawned). The entity is the one the
	 * particles follow: for a crit that is the player who got hit, not the attacker.
	 */
	public static boolean blocksEmitter(Entity entity, ParticleType<?> type) {
		if (!active) {
			return false;
		}
		ParticleCategory category = categoryOf(type);
		if (!ALLOWED[category.ordinal()]) {
			return true;
		}
		if (category == ParticleCategory.CRIT && critOnlyMine) {
			return !isMyRecentTarget(entity);
		}
		return false;
	}

	/** Particles that something adds straight to the engine (block break particles, mod stuff). */
	public static boolean blocksDirectAdd(Particle particle) {
		if (!active || insideCreate) {
			return false;
		}
		ParticleCategory category = particle instanceof TerrainParticle
			? ParticleCategory.BLOCK_PARTICLES
			: ParticleCategory.EVERYTHING_ELSE;
		return !ALLOWED[category.ordinal()];
	}

	private static ParticleCategory categoryOf(ParticleType<?> type) {
		return TYPE_CACHE.computeIfAbsent(type, ParticleFilter::classify);
	}

	private static ParticleCategory classify(ParticleType<?> type) {
		Identifier key = BuiltInRegistries.PARTICLE_TYPE.getKey(type);
		if (key == null || !"minecraft".equals(key.getNamespace())) {
			return ParticleCategory.EVERYTHING_ELSE;
		}
		return switch (key.getPath()) {
			case "totem_of_undying" -> ParticleCategory.TOTEM_POP;
			case "crit" -> ParticleCategory.CRIT;
			case "enchanted_hit" -> ParticleCategory.ENCHANTED_HIT;
			case "damage_indicator" -> ParticleCategory.DAMAGE_INDICATOR;
			case "sweep_attack" -> ParticleCategory.SWEEP;
			case "explosion", "explosion_emitter" -> ParticleCategory.EXPLOSIONS;
			case "gust", "small_gust", "gust_emitter_large", "gust_emitter_small", "dust_pillar" -> ParticleCategory.MACE_SMASH;
			case "block", "block_crumble" -> ParticleCategory.BLOCK_PARTICLES;
			default -> ParticleCategory.EVERYTHING_ELSE;
		};
	}
}
