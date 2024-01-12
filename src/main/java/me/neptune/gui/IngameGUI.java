package me.neptune.gui;

import me.neptune.Neptune;
import me.neptune.gui.tabs.Tab;
import me.neptune.module.Module;
import me.neptune.module.Module.Category;
import me.neptune.settings.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class IngameGUI extends Tab {
	private KeyBinding keybindUp;
	private KeyBinding keybindDown;
	private KeyBinding keybindLeft;
	private KeyBinding keybindRight;

	int index = 0;
	int indexMods = 0;
	boolean isCategoryMenuOpen = false;

	Category[] categories;
	ArrayList<Module> modules = new ArrayList<>();

	public IngameGUI() {
		this.keybindUp = new KeyBinding("key.tabup", GLFW.GLFW_KEY_UP, "key.categories.aoba");
		this.keybindDown = new KeyBinding("key.tabdown", GLFW.GLFW_KEY_DOWN, "key.categories.aoba");
		this.keybindLeft = new KeyBinding("key.tableft", GLFW.GLFW_KEY_LEFT, "key.categories.aoba");
		this.keybindRight = new KeyBinding("key.tabright", GLFW.GLFW_KEY_RIGHT, "key.categories.aoba");

		categories = Category.values();
		this.x = Settings.getSettingInt("x");
		this.y = Settings.getSettingInt("y");
		this.width = 150;
		this.height = 30;
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		{
			// If the click GUI is open, and the
			if (Neptune.HUD.isClickGuiOpen()) {
				if (HudManager.currentGrabbed == null) {
					if (mouseX >= (x) && mouseX <= (x + width)) {
						if (mouseY >= (y) && mouseY <= (y + height)) {
							if (mouseClicked) {
								HudManager.currentGrabbed = this;
							}
						}
					}
				}
			}
			if(HudManager.tab.getValue()) {
				if (this.keybindUp.isPressed()) {
					if (!isCategoryMenuOpen) {
						if (index == 0) {
							index = categories.length - 1;
						} else {
							index -= 1;
						}
					} else {
						if (indexMods == 0) {
							indexMods = modules.size() - 1;
						} else {
							indexMods -= 1;
						}
					}
					this.keybindUp.setPressed(false);
				} else if (this.keybindDown.isPressed()) {
					if (!isCategoryMenuOpen) {
						index = (index + 1) % categories.length;
					} else {
						indexMods = (indexMods + 1) % modules.size();
					}
					this.keybindDown.setPressed(false);
				} else if (this.keybindRight.isPressed()) {
					if (!isCategoryMenuOpen && x != -width) {
						isCategoryMenuOpen = true;
						if (modules.isEmpty()) {
							for (Module module : Neptune.MODULE.modules) {
								if (module.isCategory(this.categories[this.index])) {
									modules.add(module);
								}
							}
						}
					} else {
						modules.get(indexMods).toggle();
					}
					this.keybindRight.setPressed(false);
				} else if (this.keybindLeft.isPressed()) {
					if (this.isCategoryMenuOpen) {
						this.indexMods = 0;
						this.modules.clear();
						this.isCategoryMenuOpen = false;
					}
					this.keybindLeft.setPressed(false);
				}
			}
		}
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		// Gets the client and window.
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		Window window = mc.getWindow();

		// Draws the top bar including "Neptune x.x"
		renderUtils.drawString(drawContext, Neptune.NAME + " " + Neptune.VERSION, 8, 8, color);

		if(HudManager.tab.getValue()){
			// Draws the table including all of the categories.
			renderUtils.drawOutlinedBox(matrixStack, x, y, width, height * this.categories.length, new Color(30,30,30), 0.4f);
			// For every category, draw a cell for it.
			for (int i = 0; i < this.categories.length; i++) {
				//renderUtils.drawString(drawContext, ">>", x + width - 24, y + (height * i) + 8, color);
				// Draws the name of the category dependent on whether it is selected.
				if (this.index == i) {
					renderUtils.drawString(drawContext, "> \u00a7f" + this.categories[i].name(), x + 8, y + (height * i) + 8, color);
				} else {
					renderUtils.drawString(drawContext, this.categories[i].name(), x + 8, y + (height * i) + 8, 0xFFFFFF);
				}
			}

			// If any particular category menu is open.
			if (isCategoryMenuOpen) {
				// Draw the table underneath
				renderUtils.drawOutlinedBox(matrixStack, x + width, y + (height * this.index), 165, height * modules.size() , new Color(30,30,30), 0.4f);
				// For every mod, draw a cell for it.
				for (int i = 0; i < modules.size(); i++) {
					if (this.indexMods == i) {
						if (modules.get(i).isOn()) {
							renderUtils.drawString(drawContext, "> " + modules.get(i).getName(), x + width + 5,
									y + (i * height) + (this.index * height) + 8, color.getColorAsInt());
						} else {
							renderUtils.drawString(drawContext, "> \u00a7f" + modules.get(i).getName(), x + width + 5,
									y + (i * height) + (this.index * height) + 8, color.getColorAsInt());
						}
					} else {
						renderUtils.drawString(drawContext, modules.get(i).getName(), x + width + 5,
								y + (i * height) + (this.index * height) + 8,
								modules.get(i).isOn() ? color.getColorAsInt() : 0xFFFFFF);
					}
				}
			}
		}

		// Draws the active mods in the top right of the screen.
		int iteration = 0;
		for(int i = 0; i < Neptune.MODULE.modules.size(); i++) {
			Module mod = Neptune.MODULE.modules.get(i);
			if(mod.isOn()) {
				renderUtils.drawString(drawContext, mod.getName(),
						(float) (window.getWidth() - ((mc.textRenderer.getWidth(mod.getName()) + 5) * 2)), 10 + (iteration*20),
						color.getColorAsInt());
				iteration++;
			}
		}
	}
}
