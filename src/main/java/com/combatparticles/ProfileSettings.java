package com.combatparticles;

/**
 * What one profile keeps. true = particle is still shown, false = removed.
 * Everything not ticked is removed while the mod is enabled.
 */
public final class ProfileSettings {
	public boolean totemPop = false;
	public boolean crit = false;
	/** When crit is on: true = only crits from YOUR hits are shown, other players' crits are removed. */
	public boolean critOnlyMine = false;
	public boolean enchantedHit = false;
	public boolean damageIndicator = false;
	public boolean sweep = false;
	public boolean explosions = false;
	public boolean maceSmash = false;
	public boolean blockParticles = false;
	public boolean everythingElse = false;

	/** What you see on your own screen when your own totem pops. */
	public TotemAnimation totemAnimation = TotemAnimation.SMALL;

	public boolean allows(ParticleCategory category) {
		return switch (category) {
			case TOTEM_POP -> totemPop;
			case CRIT -> crit;
			case ENCHANTED_HIT -> enchantedHit;
			case DAMAGE_INDICATOR -> damageIndicator;
			case SWEEP -> sweep;
			case EXPLOSIONS -> explosions;
			case MACE_SMASH -> maceSmash;
			case BLOCK_PARTICLES -> blockParticles;
			case EVERYTHING_ELSE -> everythingElse;
		};
	}

	public static ProfileSettings defaultsFor(Profile profile) {
		ProfileSettings s = new ProfileSettings();
		switch (profile) {
			case SPEAR_MACE -> {
				// totem pop + crit (everyone's), small totem animation, nothing else
				s.totemPop = true;
				s.crit = true;
				s.totemAnimation = TotemAnimation.SMALL;
			}
			case MACE -> {
				// totem pop + crit (everyone's), NO totem animation, nothing else
				s.totemPop = true;
				s.crit = true;
				s.totemAnimation = TotemAnimation.OFF;
			}
			case CRYSTAL -> {
				// totem pop only, small totem animation, nothing else
				s.totemPop = true;
				s.totemAnimation = TotemAnimation.SMALL;
			}
			case SMP -> {
				// totem pop (everyone's) + only MY crits + sweep arc, small totem animation, nothing else
				s.totemPop = true;
				s.crit = true;
				s.critOnlyMine = true;
				s.sweep = true;
				s.totemAnimation = TotemAnimation.SMALL;
			}
		}
		return s;
	}
}
