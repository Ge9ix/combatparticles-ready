package com.combatparticles.sodium;

import com.combatparticles.CombatConfig;
import com.combatparticles.Profile;
import com.combatparticles.ProfileSettings;
import com.combatparticles.TotemAnimation;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** Adds the "Combat Particles" section to Sodium's Video Settings. */
public class CombatParticlesSodiumPage implements ConfigEntryPoint {
	private static final String NAMESPACE = "combatparticles";

	private record Opt(String key, String name, String tooltip,
			Function<ProfileSettings, Boolean> getter, BiConsumer<ProfileSettings, Boolean> setter) {}

	private static final List<Opt> PARTICLE_OPTIONS = List.of(
		new Opt("totem_pop", "Totem pop particles",
			"Keep the particles that burst out when a totem pops. Shown for every player.",
			s -> s.totemPop, (s, v) -> s.totemPop = v),
		new Opt("crit", "Crit particles",
			"Keep the critical hit particles.",
			s -> s.crit, (s, v) -> s.crit = v),
		new Opt("crit_only_mine", "  Crit: only my own hits",
			"Needs 'Crit particles' ticked. On = only crits from YOUR hits show, other players' crits are removed.",
			s -> s.critOnlyMine, (s, v) -> s.critOnlyMine = v),
		new Opt("enchanted_hit", "Enchanted hit particles",
			"Keep the blue sparkles from enchanted weapon hits.",
			s -> s.enchantedHit, (s, v) -> s.enchantedHit = v),
		new Opt("damage_indicator", "Damage indicator hearts",
			"Keep the dark red hearts that appear when you deal damage.",
			s -> s.damageIndicator, (s, v) -> s.damageIndicator = v),
		new Opt("sweep", "Sweep attack particles",
			"Keep the white sweeping arc from sword swings.",
			s -> s.sweep, (s, v) -> s.sweep = v),
		new Opt("explosions", "Explosion particles",
			"Keep explosion particles (end crystals, TNT, creepers).",
			s -> s.explosions, (s, v) -> s.explosions = v),
		new Opt("mace_smash", "Mace smash particles",
			"Keep the gust and dust pillar particles from mace smash attacks.",
			s -> s.maceSmash, (s, v) -> s.maceSmash = v),
		new Opt("block_particles", "Block break particles",
			"Keep the debris particles from breaking or hitting blocks.",
			s -> s.blockParticles, (s, v) -> s.blockParticles = v),
		new Opt("everything_else", "All other particles",
			"Keep every particle not listed above (smoke, flames, potions, rain, redstone, other mods...).",
			s -> s.everythingElse, (s, v) -> s.everythingElse = v)
	);

	@Override
	public void registerConfigLate(ConfigBuilder builder) {
		ModOptionsBuilder mod = builder.registerOwnModOptions();
		mod.addPage(generalPage(builder));
		for (Profile profile : Profile.values()) {
			mod.addPage(profilePage(builder, profile));
		}
	}

	private OptionPageBuilder generalPage(ConfigBuilder builder) {
		OptionGroupBuilder group = builder.createOptionGroup()
			.setName(Component.literal("Combat Particles"))
			.addOption(builder.createBooleanOption(id("enabled"))
				.setName(Component.literal("Enable Combat Particles"))
				.setTooltip(Component.literal("Off = completely vanilla particles and totem animation."))
				.setStorageHandler(CombatConfig::saveAndApply)
				.setBinding(v -> CombatConfig.get().enabled = v, () -> CombatConfig.get().enabled)
				.setDefaultValue(true))
			.addOption(builder.createEnumOption(id("active_profile"), Profile.class)
				.setName(Component.literal("Active profile"))
				.setTooltip(Component.literal("Pick the kit you are playing. Each kit has its own page on the left."))
				.setStorageHandler(CombatConfig::saveAndApply)
				.setBinding(v -> CombatConfig.get().activeProfile = v, () -> CombatConfig.get().activeProfile)
				.setDefaultValue(Profile.SPEAR_MACE)
				.setElementNameProvider(p -> Component.literal(p.displayName)));

		OptionGroupBuilder totem = builder.createOptionGroup()
			.setName(Component.literal("Small totem animation"))
			.addOption(builder.createIntegerOption(id("small_totem_size"))
				.setName(Component.literal("Small totem size (%)"))
				.setTooltip(Component.literal("Size of the small totem icon. 100 = same size as a hotbar item."))
				.setStorageHandler(CombatConfig::saveAndApply)
				.setBinding(v -> CombatConfig.get().smallTotemSizePercent = v, () -> CombatConfig.get().smallTotemSizePercent)
				.setRange(50, 300, 25)
				.setDefaultValue(100));

		return builder.createOptionPage()
			.setName(Component.literal("Combat Particles"))
			.addOptionGroup(group)
			.addOptionGroup(totem);
	}

	private OptionPageBuilder profilePage(ConfigBuilder builder, Profile profile) {
		ProfileSettings defaults = ProfileSettings.defaultsFor(profile);

		OptionGroupBuilder particles = builder.createOptionGroup()
			.setName(Component.literal("Particles to keep (everything unticked is removed)"));
		for (Opt opt : PARTICLE_OPTIONS) {
			particles.addOption(builder.createBooleanOption(id(profile.id + "/" + opt.key()))
				.setName(Component.literal(opt.name()))
				.setTooltip(Component.literal(opt.tooltip()))
				.setStorageHandler(CombatConfig::saveAndApply)
				.setBinding(
					v -> opt.setter().accept(CombatConfig.get().settings(profile), v),
					() -> opt.getter().apply(CombatConfig.get().settings(profile)))
				.setDefaultValue(opt.getter().apply(defaults)));
		}

		OptionGroupBuilder animation = builder.createOptionGroup()
			.setName(Component.literal("When my totem pops"))
			.addOption(builder.createEnumOption(id(profile.id + "/totem_animation"), TotemAnimation.class)
				.setName(Component.literal("Totem animation"))
				.setTooltip(Component.literal("Vanilla = big floating totem. Small = tiny icon above your crosshair. Removed = nothing. Only affects your own pop."))
				.setStorageHandler(CombatConfig::saveAndApply)
				.setBinding(
					v -> CombatConfig.get().settings(profile).totemAnimation = v,
					() -> CombatConfig.get().settings(profile).totemAnimation)
				.setDefaultValue(defaults.totemAnimation)
				.setElementNameProvider(a -> Component.literal(a.displayName)));

		return builder.createOptionPage()
			.setName(Component.literal(profile.displayName))
			.addOptionGroup(particles)
			.addOptionGroup(animation);
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(NAMESPACE, path);
	}
}
