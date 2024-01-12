/**
 * Anti-Invis Module
 */
package me.neptune.module.modules.combat;

import me.neptune.module.Module;

public class AntiInvis extends Module {
	public static AntiInvis INSTANCE;
	public AntiInvis() {
		super("AntiInvis", Category.Combat);
		this.setDescription("Reveals players who are invisible.");
		INSTANCE = this;
	}

}