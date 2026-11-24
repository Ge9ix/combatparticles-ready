package com.combatparticles;

/** What happens on your own screen when YOUR totem pops. */
public enum TotemAnimation {
	/** Normal big floating totem. */
	VANILLA("Vanilla (big)"),
	/** Tiny totem icon above your crosshair instead of the big one. */
	SMALL("Small"),
	/** Nothing at all. */
	OFF("Removed");

	public final String displayName;

	TotemAnimation(String displayName) {
		this.displayName = displayName;
	}
}
