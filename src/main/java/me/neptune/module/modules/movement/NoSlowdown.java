/**
 * NoSlowdown Module
 */
package me.neptune.module.modules.movement;

import me.neptune.module.Module;

public class NoSlowdown extends Module {
	public static NoSlowdown INSTANCE;
	public NoSlowdown() {
		super("NoSlowDown", Category.Movement);
		this.setDescription("Prevents the player from being slowed down by blocks.");
		INSTANCE = this;
	}
}
