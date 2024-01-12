/**
 * A class to represent a ClickGui Tab that contains different Components.
 */

package me.neptune.gui.tabs;

import me.neptune.Neptune;
import me.neptune.gui.ClickUI;
import me.neptune.gui.Color;
import me.neptune.gui.HudManager;
import me.neptune.gui.elements.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;


public class ClickGuiTab extends Tab {
	protected String title;
	protected boolean isPinned = false;
	protected boolean drawBorder = true;
	protected boolean inheritHeightFromChildren = true;
	
	protected ArrayList<Component> children = new ArrayList<>();

	public ClickGuiTab(String title, int x, int y) {
		this.title = title;
		this.x = x;
		this.y = y;
		this.width = 180;
		this.mc = MinecraftClient.getInstance();
	}

	public final String getTitle() {
		return title;
	}

	public final boolean isPinned() {
		return this.isPinned;
	}

	public final void setPinned(boolean pin) {
		this.isPinned = pin;
	}
	public final void setTitle(String title) {

		this.title = title;
	}

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}

	public final int getWidth() {
		return width;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final int getHeight() {
		return height;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final boolean getPinned() {
		return this.isPinned;
	}

	public final boolean isGrabbed() {
		return (HudManager.currentGrabbed == this);
	}

	public final void addChild(Component component) {
		this.children.add(component);
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if(this.inheritHeightFromChildren) {
			int tempHeight = 1;
			for (Component child : children) {
				tempHeight += (child.getHeight());
			}
			this.height = tempHeight;
		}

		onMouseClick(mouseX, mouseY, mouseClicked);
	}

	public void onMouseClick(double mouseX, double mouseY, boolean mouseClicked) {
		if (Neptune.HUD.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + 28)) {
						if (mouseClicked) {
							boolean isInsidePinButton = false;
							if (mouseX >= (x + width - 24) && mouseX <= (x + width - 2)) {
								if (mouseY >= (y + 4) && mouseY <= (y + 20)) {
									isInsidePinButton = true;
								}
							}
							if (isInsidePinButton) {
									ClickUI.clicked = false;
									this.isPinned = !this.isPinned;
									return;
							} else {
								HudManager.currentGrabbed = this;
							}
						}
					}
				}
			}
			int i = 30;
			for (Component child : this.children) {
				child.update(i, mouseX, mouseY, mouseClicked);
				i += child.getHeight();
			}
		}
	}
	public double animate(double value, double target) {
		double add = (target - value) / ((double) (2 * Math.max(Math.min(240, MinecraftClient.getInstance().getCurrentFps()), 120)) / 60);
		if (Math.abs(add) <= 1) return target;
		return value + add;
	}
	private double currentHeight = 0;
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		if(this.inheritHeightFromChildren) {
			int tempHeight = 1;
			for (Component child : children) {
				tempHeight += (child.getHeight());
			}
			this.height = tempHeight;
		}

		MatrixStack matrixStack = drawContext.getMatrices();
		currentHeight = animate(currentHeight, height);
		if(drawBorder) {
			// Draws background depending on components width and height
			renderUtils.drawOutlinedBox(matrixStack, x, y, width, 29, new Color(30,30,30), 0.4f);
			renderUtils.drawString(drawContext, this.title, x + 8, y + 8, Neptune.HUD.getColor());
			renderUtils.drawOutlinedBox(matrixStack, x, y + 29, width, (int) currentHeight, new Color(30,30,30), 0.4f);
			if (this.isPinned) {
				renderUtils.drawOutlinedBox(matrixStack, x + width - 24, y + 4, 20, 20, new Color(30,30,30), 0.8f);
			} else {
				renderUtils.drawOutlinedBox(matrixStack, x + width - 24, y + 4, 20, 20, new Color(30,30,30), 0.2f);
			}
		}
		int i = 30;
		for (Component child : children) {
			child.draw(i, drawContext, partialTicks, color);
			i += child.getHeight();
		}
	}
}
