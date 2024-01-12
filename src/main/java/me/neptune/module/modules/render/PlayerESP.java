/**
 * PlayerESP Module
 */
package me.neptune.module.modules.render;

import me.neptune.gui.Color;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.CombatUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

public class PlayerESP extends Module {

	private int r;
	private int g;
	private int b;
	private Color color;
	private BooleanSetting hueSwitch = new BooleanSetting("Hue", "playeresp_switchhue", true);
	private SliderSetting hue = new SliderSetting("Hue", "playeresp_hue", 4f, 0f, 360f, 1);
	private SliderSetting alpha = new SliderSetting("Alpha", "playeresp_alpha", 150f, 0f, 360f, 1);
	private BooleanSetting white = new BooleanSetting("White", "playeresp_white", false);

	public PlayerESP() {
		super("PlayerESP", Category.Render);
		this.setDescription("Allows the player to see other players with an ESP.");

		this.addSetting(hue);
		this.addSetting(hueSwitch);
		this.addSetting(alpha);
		this.addSetting(white);
	}
	@Override
	public void onUpdate(){
		if(hueSwitch.getValue()) {
			color.setHSV(hue.getValueInt(), 1, 1);
		}

    }
    @Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		if (mc.world != null) {
			for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
				if(entity != mc.player) {
					if(white.getValue()){
						this.getRenderUtils().draw3DBox(matrixStack, mc.player.getBoundingBox().offset(CombatUtil.getMotionVec(mc.player, 1, true)), 255, 255, 255, 2);
					}else{
						this.getRenderUtils().draw3DBox(matrixStack, mc.player.getBoundingBox().offset(CombatUtil.getMotionVec(mc.player, 1, true)), color.r, color.g, color.b, 2);
					}
				}
			}
		}
	}

}
