/**
 * NoFall Module
 */
package me.neptune.module.modules.movement;

import me.neptune.events.impl.PacketEvent;
import me.neptune.mixin.IPlayerMoveC2SPacket;
import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;

public class NoFall extends Module {
	private final SliderSetting distance =
			new SliderSetting("Distance", 3.0f, 0.0f, 8.0f, 0.1);
	public NoFall() {
		super("NoFall", Category.Movement);
		this.setDescription("Prevents fall damage.");
		addSetting(distance);
	}

	@Override
	public void onUpdate() {
		if(mc.player.fallDistance >= distance.getValue() - 0.1) {
			mc.player.networkHandler.sendPacket(new OnGroundOnly(true));
		}
    }

	@EventHandler
	public void onPacketSend(PacketEvent.Send event) {
		if (nullCheck()) {
			return;
		}
		for (ItemStack is : mc.player.getArmorItems()) {
			if (is.getItem() == Items.ELYTRA) {
				return;
			}
		}
		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			if (mc.player.fallDistance >= (float) this.distance.getValue()) {
				((IPlayerMoveC2SPacket) packet).setOnGround(true);
			}
		}
	}
}
