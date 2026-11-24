package com.combatparticles;

/** The kits you can pick from in the settings page. Add more here if you want more kits. */
public enum Profile {
	SPEAR_MACE("spear_mace", "Spear Mace"),
	MACE("mace", "Mace"),
	CRYSTAL("crystal", "Crystal"),
	SMP("smp", "SMP");

	/** Used in option ids, so lowercase letters, digits and underscores only. */
	public final String id;
	public final String displayName;

	Profile(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}
}
