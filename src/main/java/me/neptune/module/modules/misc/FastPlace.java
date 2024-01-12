/**
 * FastPlace Module
 */
package me.neptune.module.modules.misc;

import me.neptune.module.Module;

public class FastPlace extends Module {
	public FastPlace() {
		super("FastPlace", Category.Misc);
		this.setDescription("Places blocks exceptionally fast");
	}

	@Override
	public void onDisable() {
		imc.setItemUseCooldown(4);
	}

    @Override
	public void onUpdate() {
		imc.setItemUseCooldown(0);
    }

}
