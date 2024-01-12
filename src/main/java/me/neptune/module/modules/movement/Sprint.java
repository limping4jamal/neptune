/**
 * Sprint Module
 */
package me.neptune.module.modules.movement;

import me.neptune.module.Module;
import me.neptune.utils.MovementUtil;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", Category.Movement);
		this.setDescription("Permanently keeps player in sprinting mode.");
	}

	@Override
	public void onUpdate() {
		mc.player.setSprinting(MovementUtil.isMoving() && !mc.player.isSneaking() && mc.player.getHungerManager().getFoodLevel() > 6);
    }
}
