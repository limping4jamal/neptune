/**
 * Reach Module
 */
package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;

public class Reach extends Module {
	public static Reach INSTANCE;
	private SliderSetting distance;

	public Reach() {
		super("Reach", Category.Combat);
		this.setDescription("Reaches further.");

		distance = new SliderSetting("Distance", "reach_distance", 5f, 1f, 15f, 0.5f);
		this.addSetting(distance);
		INSTANCE = this;
	}

	public float getReach() {
		return distance.getValueFloat();
	}

	public void setReachLength(float reach) {
		this.distance.setValue(reach);
	}
}