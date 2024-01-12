/**
 * AutoFish Module
 */
package me.neptune.module.modules.misc;

import me.neptune.events.impl.PacketEvent;
import me.neptune.module.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class AutoFish extends Module {
	public AutoFish() {
		super("AutoFish", Category.Misc);
		this.setDescription("Automatically fishes for you.");
	}

	@EventHandler
	public void onReceivePacket(PacketEvent.Receive event) {
		Packet<?> packet = event.getPacket();
		if(packet instanceof PlaySoundS2CPacket soundPacket) {
			if(soundPacket.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
				recastRod();
			}
		}
	}
	
	private void recastRod() {
		
		PlayerInteractItemC2SPacket packetTryUse = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0);
		mc.player.networkHandler.sendPacket(packetTryUse);
		mc.player.networkHandler.sendPacket(packetTryUse);
	}

}
