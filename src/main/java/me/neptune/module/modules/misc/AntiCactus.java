/**
 * AntiCactus Module
 */
package me.neptune.module.modules.misc;

import me.neptune.module.Module;

public class AntiCactus extends Module {
	public static AntiCactus INSTANCE;
	public AntiCactus() {
		super("AntiCactus", Category.Misc);
		this.setDescription("Prevents blocks from hurting you.");
		INSTANCE = this;
	}
}