package com.combatparticles;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;

/**
 * The small replacement for the big floating totem. Draws a tiny totem icon just above your
 * crosshair that pops in, drifts up a little and shrinks away. Only ever shown for your own pop,
 * because vanilla only shows the animation for you.
 */
public final class SmallTotem {
	private static final long DURATION_MS = 1100;

	private static ItemStack stack = ItemStack.EMPTY;
	private static long startMs = 0;
	private static boolean running = false;

	private SmallTotem() {}

	public static void start(ItemStack popped) {
		stack = popped == null || popped.isEmpty() ? new ItemStack(Items.TOTEM_OF_UNDYING) : popped.copy();
		startMs = System.currentTimeMillis();
		running = true;
	}

	public static void render(GuiGraphics graphics) {
		if (!running) {
			return;
		}
		long elapsed = System.currentTimeMillis() - startMs;
		if (elapsed >= DURATION_MS) {
			running = false;
			return;
		}
		float t = elapsed / (float) DURATION_MS;
		// quick pop in, hold, then shrink away
		float grow = t < 0.15f ? t / 0.15f : (t > 0.7f ? (1.0f - t) / 0.3f : 1.0f);
		float scale = CombatConfig.get().smallTotemSizePercent / 100.0f * grow;
		if (scale < 0.05f) {
			return;
		}
		float centerX = graphics.guiWidth() / 2.0f;
		float centerY = graphics.guiHeight() / 2.0f - 34.0f - 12.0f * t;

		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(centerX, centerY);
		pose.scale(scale, scale);
		pose.translate(-8.0f, -8.0f); // items are 16x16
		graphics.renderItem(stack, 0, 0);
		pose.popMatrix();
	}
}
