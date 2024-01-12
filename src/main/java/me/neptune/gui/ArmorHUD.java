/**
 * A class to represent a Tab containing Armor Information
 */

package me.neptune.gui;

import me.neptune.Neptune;
import me.neptune.gui.tabs.Tab;
import me.neptune.settings.Settings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ArmorHUD extends Tab{

	public ArmorHUD() {
		this.width = 64;
		this.height = 128;
		this.x = (int) Settings.getSettingFloat("armor_x", 300);
		this.y = (int) Settings.getSettingFloat("armor_y", 300);
	}
	
	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
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
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		DefaultedList<ItemStack> armors = mc.player.getInventory().armor;
		int xOff = 16;
		for(ItemStack armor : armors) {
			if(armor.getItem() == Items.AIR) continue;
			MatrixStack matrixStack = drawContext.getMatrices();
			matrixStack.push();
			matrixStack.translate(-x, -y - height, 0);
			matrixStack.scale(2, 2, 0);
			drawContext.drawItem(armor, this.x, this.y + this.width - xOff);
			matrixStack.pop();
			xOff += 16;
		}
	}
}
