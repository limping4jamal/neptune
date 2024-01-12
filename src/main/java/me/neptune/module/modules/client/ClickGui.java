/**
 * Anti-Invis Module
 */
package me.neptune.module.modules.client;

import me.neptune.gui.ClickUI;
import me.neptune.gui.HudManager;
import me.neptune.module.Module;
import me.neptune.settings.EnumSetting;
import me.neptune.utils.FadeUtils;

public class ClickGui extends Module {
	public static ClickGui INSTANCE;
	public EnumSetting mode = new EnumSetting("Mode", Mode.Pull);
	public ClickGui() {
		super("ClickGui", Category.Client);
		INSTANCE = this;
		addSetting(mode);
	}

	public static final FadeUtils fade = new FadeUtils(500);

	@Override
	public void onUpdate() {
		if (!(mc.currentScreen instanceof ClickUI)) {
			disable();
		}
    }

	@Override
	public void onEnable() {
		fade.reset();
		if (nullCheck()) {
			disable();
			return;
		}
		mc.setScreen(HudManager.clickGui);
	}

	@Override
	public void onDisable() {
		if (mc.currentScreen instanceof ClickUI) {
			mc.setScreen(null);
		}
	}

	public enum Mode {
		Scale, Pull
	}
}