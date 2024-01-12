/**
 * Fullbright Module
 */
package me.neptune.module.modules.render;

import me.neptune.module.Module;

public class Fullbright extends Module {
	public static Fullbright INSTANCE;
	public Fullbright() {
		super("FullBright", Category.Render);
		this.setDescription("Maxes out the brightness.");
		INSTANCE = this;
	}
}
