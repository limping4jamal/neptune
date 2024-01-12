/**
 * A class to represent a generic module.
 */
package me.neptune.module;

import me.neptune.Neptune;
import me.neptune.cmd.CommandManager;
import me.neptune.settings.BindSetting;
import me.neptune.settings.Setting;
import me.neptune.utils.RenderUtils;
import me.neptune.utils.Wrapper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class Module implements Wrapper {

	public Module(String name, Category category) {
		this.name = name;
		this.category = category;
		setDescription(name);
		ModuleManager.lastLoadModule = this;
		keybind = new BindSetting(getName(), getName().toLowerCase() + "_bind", name.equalsIgnoreCase("ClickGui") ? GLFW.GLFW_KEY_RIGHT_SHIFT : -1);
		addSetting(keybind);
	}
	private String name;
	private String description;
	private final Category category;
	private final BindSetting keybind;
	private boolean state;
	private final RenderUtils renderUtils = new RenderUtils();

	private final List<Setting> settings = new ArrayList<>();

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return this.category;
	}

	public BindSetting getBind() {
		return this.keybind;
	}


	public boolean isOn() {
		return this.state;
	}

	public void toggle() {
		if (this.isOn()) {
			disable();
		} else {
			enable();
		}
	}

	public void enable() {
		if (state) return;
		state = true;
		Neptune.EVENT_BUS.subscribe(this);
		//this.onToggle();
		onEnable();
	}

	public void disable() {
		if (!state) return;
		state = false;
		Neptune.EVENT_BUS.unsubscribe(this);
		//this.onToggle();
		onDisable();
	}
	public void setState(boolean state) {
		if (state == state) return;
		if (state) {
			enable();
		} else {
			disable();
		}
	}

	public boolean setBind(String rkey) {
		if (rkey.equalsIgnoreCase("none")) {
			this.keybind.setKey(-1);
			return true;
		}
		int key;
		try {
			key = InputUtil.fromTranslationKey("key.keyboard." + rkey.toLowerCase()).getCode();
		} catch (NumberFormatException e) {
			if (!nullCheck()) CommandManager.sendChatMessage("\u00a7c[!] \u00a7fBad key!");
			return false;
		}
		if (rkey.equalsIgnoreCase("none")) {
			key = -1;
		}
		if (key == 0) {
			return false;
		}
		this.keybind.setKey(key);
		return true;
	}

	public <T extends Setting> T addSetting(T setting) {
		this.settings.add(setting);
		return setting;
	}

	public List<Setting> getSettings() {
		return this.settings;
	}

	public RenderUtils getRenderUtils() {
		return this.renderUtils;
	}

	public boolean hasSettings() {
		return !this.settings.isEmpty();
	}

	public static boolean nullCheck() {
		return mc.player == null || mc.world == null;
	}

	public void onDisable() {
		CommandManager.sendChatMessage("\u00a7c[-] \u00a7f" + getName());
	}

	public void onEnable() {
		CommandManager.sendChatMessage("\u00a7a[+] \u00a7f" + getName());
	}


	public void onToggle() {
	}

	public void onUpdate() {
    }

	public void onRender(MatrixStack matrixStack, float partialTicks) {
	}

	public void onSendPacket(Packet<?> packet) {

	}

	public final boolean isCategory(Category category) {
		return category == this.category;
	}

	public enum Category {
		Combat, Movement, Render, World, Misc, Client
	}
}
