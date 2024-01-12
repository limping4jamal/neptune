package me.neptune.gui.tabs;

import me.neptune.gui.HudManager;
import me.neptune.gui.elements.CheckboxComponent;
import me.neptune.gui.elements.SliderComponent;
import me.neptune.gui.elements.StringComponent;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.SliderSetting;

public class OptionsTab extends ClickGuiTab {

	private StringComponent uiSettingsString = new StringComponent("UI Settings", this);
	
	public OptionsTab(String title, int x, int y) {
		super(title, x, y);
		this.setWidth(180);
		this.addChild(uiSettingsString);
	}

	public void addChild(SliderSetting sliderSetting) {
		HudManager.SETTINGS.add(sliderSetting);
		addChild(new SliderComponent(this, sliderSetting));
	}

	public void addChild(BooleanSetting booleanSetting) {
		HudManager.SETTINGS.add(booleanSetting);
		addChild(new CheckboxComponent(this, booleanSetting));
	}
}
