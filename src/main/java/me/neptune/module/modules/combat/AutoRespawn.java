/**
 * AutoRespawn Module
 */
package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn extends Module{
	
	public AutoRespawn() {
		super("AutoRespawn", Category.Combat);
		this.setDescription("Automatically respawns when you die.");
	}

	@Override
	public void onUpdate() {
		if (mc.currentScreen instanceof DeathScreen) {
			mc.player.requestRespawn();
			mc.setScreen(null);
		}
    }
}
