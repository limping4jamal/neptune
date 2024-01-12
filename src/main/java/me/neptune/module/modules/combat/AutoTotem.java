/**
 * AutoTotem Module
 */
package me.neptune.module.modules.combat;

import me.neptune.events.impl.UpdateWalkingEvent;
import me.neptune.gui.ClickUI;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.InventoryUtil;
import me.neptune.utils.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.lang.reflect.Field;

public class AutoTotem extends Module {
	private final BooleanSetting mainHand =
			new BooleanSetting("MainHand",false);
	private final SliderSetting health =
			new SliderSetting("Health", "autototem_health", 16.0f, 0.0f, 36.0f, 0.1);
	public AutoTotem() {
		super("AutoTotem", Category.Combat);
		this.setDescription("Automatically replaced totems.");
		try {
			for (Field field : AutoTotem.class.getDeclaredFields()) {
				if (!Setting.class.isAssignableFrom(field.getType()))
					continue;
				Setting setting = (Setting) field.get(this);
				addSetting(setting);
			}
		} catch (Exception e) {
		}
	}

	private final Timer timer = new Timer().reset();

	@EventHandler
	public void onUpdateWalking(UpdateWalkingEvent event) {
		update();
	}

	@Override
	public void onUpdate() {
		update();
    }

	private void update() {
		if (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen) && !(mc.currentScreen instanceof InventoryScreen) && !(mc.currentScreen instanceof ClickUI)) {
			return;
		}
		if (!timer.passedMs(200)) {
			return;
		}
		if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > health.getValue()) {
			return;
		}
		if (mc.player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING || mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
			return;
		}
		int itemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
		if (itemSlot != -1) {
			if (itemSlot < 9) {
				itemSlot += 36;
			}
			if (mainHand.getValue()) {
				InventoryUtil.doSwap(0);
				if (mc.player.getInventory().getStack(0).getItem() != Items.TOTEM_OF_UNDYING) {
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, mc.player);
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36, 0, SlotActionType.PICKUP, mc.player);
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, mc.player);
					mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
				}
			} else {
				mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, mc.player);
				mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
				mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, mc.player);
				mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
			}
			timer.reset();
		}
	}
}
