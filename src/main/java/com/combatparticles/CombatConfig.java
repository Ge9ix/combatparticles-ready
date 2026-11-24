package com.combatparticles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saved to config/combatparticles.json. */
public final class CombatConfig {
	/** Bump when defaults change so old files are reset once. */
	private static final int CURRENT_VERSION = 2;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static CombatConfig instance;

	/** Which version of the mod wrote this file. 0 = file from before versions existed. */
	public int configVersion = 0;

	/** Master switch. false = the mod does nothing, vanilla particles and totem animation. */
	public boolean enabled = true;
	public Profile activeProfile = Profile.SPEAR_MACE;
	/** Size of the small totem animation. 100 = same size as a hotbar item. */
	public int smallTotemSizePercent = 100;
	public ProfileSettings spearMace = ProfileSettings.defaultsFor(Profile.SPEAR_MACE);
	public ProfileSettings mace = ProfileSettings.defaultsFor(Profile.MACE);
	public ProfileSettings crystal = ProfileSettings.defaultsFor(Profile.CRYSTAL);
	public ProfileSettings smp = ProfileSettings.defaultsFor(Profile.SMP);

	public static CombatConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public ProfileSettings settings(Profile profile) {
		return switch (profile) {
			case SPEAR_MACE -> spearMace;
			case MACE -> mace;
			case CRYSTAL -> crystal;
			case SMP -> smp;
		};
	}

	public ProfileSettings activeSettings() {
		return settings(activeProfile);
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("combatparticles.json");
	}

	public static void load() {
		CombatConfig loaded = null;
		Path path = path();
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				loaded = GSON.fromJson(reader, CombatConfig.class);
			} catch (Exception e) {
				System.err.println("[combatparticles] Could not read config, using defaults: " + e);
			}
		}
		if (loaded == null || loaded.configVersion < CURRENT_VERSION) {
			// missing, broken, or written by an older version: start from the new defaults
			loaded = new CombatConfig();
		}
		loaded.configVersion = CURRENT_VERSION;
		// Fill anything missing (hand-edited files)
		if (loaded.activeProfile == null) loaded.activeProfile = Profile.SPEAR_MACE;
		if (loaded.spearMace == null) loaded.spearMace = ProfileSettings.defaultsFor(Profile.SPEAR_MACE);
		if (loaded.mace == null) loaded.mace = ProfileSettings.defaultsFor(Profile.MACE);
		if (loaded.crystal == null) loaded.crystal = ProfileSettings.defaultsFor(Profile.CRYSTAL);
		if (loaded.smp == null) loaded.smp = ProfileSettings.defaultsFor(Profile.SMP);
		for (Profile profile : Profile.values()) {
			ProfileSettings s = loaded.settings(profile);
			if (s.totemAnimation == null) s.totemAnimation = TotemAnimation.SMALL;
		}
		loaded.smallTotemSizePercent = Math.max(25, Math.min(400, loaded.smallTotemSizePercent));
		instance = loaded;
		save();
	}

	public static void save() {
		Path path = path();
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(get(), writer);
			}
		} catch (IOException e) {
			System.err.println("[combatparticles] Could not save config: " + e);
		}
	}

	/** Called by Sodium after the settings screen applies changes. */
	public static void saveAndApply() {
		save();
		ParticleFilter.rebuild();
	}
}
