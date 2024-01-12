package me.neptune.gui.elements;

import me.neptune.Neptune;
import me.neptune.gui.ClickUI;
import me.neptune.gui.Color;
import me.neptune.gui.HudManager;
import me.neptune.gui.tabs.ClickGuiTab;
import me.neptune.module.Module;
import me.neptune.settings.*;
import me.neptune.utils.Wrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleComponent extends Component implements Wrapper {

	private final String text;
	private final Module module;
	private final ClickGuiTab parent;
	private boolean popped = false;

	private int expandedHeight = 30;
	
	private final Color hoverColor = new Color(90, 90, 90);
	private final Color color = new Color(128, 128, 128);

	private Color backgroundColor = color;

	private final List<Component> settingsList = new ArrayList<>();

	public ModuleComponent(String text, ClickGuiTab parent, Module module) {
		super();
		this.text = text;
		this.parent = parent;
		this.module = module;
		for (Setting setting : this.module.getSettings()) {
			Component c;
			if (setting instanceof SliderSetting) {
				c = new SliderComponent(this.parent, (SliderSetting) setting);
			} else if (setting instanceof BooleanSetting) {
				c = new CheckboxComponent(this.parent, (BooleanSetting) setting);
			} else if (setting instanceof ListSetting) {
				c = new ListComponent(this.parent, (ListSetting) setting);
			} else if (setting instanceof BindSetting) {
				c = new BindComponent(this.parent, (BindSetting) setting);
			} else if (setting instanceof EnumSetting) {
				c = new EnumComponent(this.parent, (EnumSetting) setting);
			} else {
				c = null;
			}
			settingsList.add(c);
		}
		
		RecalculateExpandedHeight();
	}

	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		// If the Module options are popped, display all of the options.
		if (this.popped) {
			// Updates all of the options. 
			int i = offset + 30;
			for (Component children : this.settingsList) {
				children.update(i, mouseX, mouseY, mouseClicked);
				i += children.getHeight();
			}
		}


		// Check if the current Module Component is currently hovered over.
		boolean hovered = ((mouseX >= parentX && mouseX <= (parentX + parentWidth)) && (mouseY >= parentY + offset && mouseY <= (parentY + offset + 28)));
		if (hovered && HudManager.currentGrabbed == null) {
			backgroundColor = hoverColor;
			if (mouseClicked) {
				ClickUI.clicked = false;
				module.toggle();
			}

			if (ClickUI.rightClicked) {
				ClickUI.rightClicked = false;
				this.popped = !this.popped;
				if (this.popped) {
					this.setHeight(expandedHeight);
				} else {
					this.setHeight(30);
				}
			}
		} else {
			backgroundColor = color;
		}
	}

	boolean isPopped = false;
	double currentHeight = 0;

	@Override
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		MatrixStack matrixStack = drawContext.getMatrices();
		currentOffset = animate(currentOffset, offset);
		currentHeight = animate(currentHeight, getHeight());
		renderUtils.drawOutlinedBox(matrixStack, parentX + 2, (int) (parentY + currentOffset), parentWidth - 4, (int) (currentHeight - 2),
				backgroundColor, 0.2f);

		if (this.popped) {
			isPopped = true;
			int i = offset + 30;
			for (Component children : this.settingsList) {
				if (children.isVisible()) {
					children.draw(i, drawContext, partialTicks, color);
					i += children.getHeight();
				}
			}
		} else if (isPopped) {
			boolean finish2 = true;
			boolean finish = false;
			for (Component children : this.settingsList) {
				if (children.isVisible()) {
					if (children.unPoppedDraw((int) currentOffset, drawContext, partialTicks, color)) {
						finish = true;
					} else {
						finish2 = false;
					}
				}
			}
			if (finish && finish2) {
				isPopped = false;
			}
		} else {
			for (Component children : this.settingsList) {
				children.currentOffset = currentOffset;
			}
		}

		renderUtils.drawString(drawContext, this.text, (float) (parentX + 8), (float) (parentY + 8 + currentOffset),
				module.isOn() ? color.getColorAsInt() : 0xFFFFFF);
		if (Neptune.HUD.gear.getValue()) {
			renderUtils.drawString(drawContext, this.popped ? "-" : "+", parentX + parentWidth - 22,
					parentY + 8 + currentOffset, color.getColorAsInt());
		}
	}
	
	public void RecalculateExpandedHeight() {
		int height = 30;
		for (Component children : this.settingsList) {
			if (children.isVisible()) {
				height += children.getHeight();
			}
		}
		expandedHeight = height;
	}
}
