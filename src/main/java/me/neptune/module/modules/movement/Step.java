/**
 * Step Module
 */
package me.neptune.module.modules.movement;

import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;

public class Step extends Module {

	private SliderSetting stepHeight;
	
	public Step() {
		super("Step", Category.Movement);
		this.setDescription("Steps up blocks.");
		
		stepHeight = new SliderSetting("Height", "step_height", 1f, 0.0f, 10f, 0.5f);
		this.addSetting(stepHeight);
	}

	@Override
	public void onDisable() {
		if (nullCheck()) return;
		mc.player.setStepHeight(.5f);
	}

	@Override
	public void onUpdate() {
		if (nullCheck())
		mc.player.setStepHeight(stepHeight.getValueFloat());;
    }
}
