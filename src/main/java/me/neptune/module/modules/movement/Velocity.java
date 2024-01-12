/**
 * AntiKnockback Module
 */
package me.neptune.module.modules.movement;

import me.neptune.events.impl.PacketEvent;
import me.neptune.mixin.IEntityVelocityUpdateS2CPacket;
import me.neptune.mixin.IExplosionS2CPacket;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

import java.lang.reflect.Field;

public class Velocity extends Module {
	public static Velocity INSTANCE;
	private final SliderSetting horizontal = new SliderSetting("Horizontal", "velocity_horizontal", 0f, 0f, 100f, 1f);
	private final SliderSetting vertical = new SliderSetting("Vertical", "velocity_vertical", 0f, 0f, 100f, 1f);
	public final BooleanSetting waterPush = new BooleanSetting("WaterPush", "velocity_waterpush", true);
	public final BooleanSetting entityPush = new BooleanSetting("EntityPush", "velocity_entitypush", true);
	public final BooleanSetting blockPush = new BooleanSetting("BlockPush", "velocity_blockpush", true);
	public Velocity() {
		super("Velocity", Category.Movement);
		this.setDescription("Prevents knockback.");

		try {
			for (Field field : Velocity.class.getDeclaredFields()) {
				if (!Setting.class.isAssignableFrom(field.getType()))
					continue;
				Setting setting = (Setting) field.get(this);
				addSetting(setting);
			}
		} catch (Exception e) {
		}
		INSTANCE = this;
	}

	@EventHandler
	public void onReceivePacket(PacketEvent.Receive event) {
		if (nullCheck()) return;
		float h = horizontal.getValueFloat() / 100;
		float v = vertical.getValueFloat() / 100;

		if (event.getPacket() instanceof EntityStatusS2CPacket packet && (packet = event.getPacket()).getStatus() == 31 && packet.getEntity(mc.world) instanceof FishingBobberEntity fishHook) {
			if (fishHook.getHookedEntity() == mc.player) {
				event.setCancel(true);
			}
		}

		if (event.getPacket() instanceof ExplosionS2CPacket) {
			IExplosionS2CPacket packet = event.getPacket();

			packet.setX(packet.getX() * h);
			packet.setY(packet.getY() * v);
			packet.setZ(packet.getZ() * h);
			return;
		}

		if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
			if (packet.getId() == mc.player.getId()) {
				if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
					event.setCancel(true);
				} else {
					((IEntityVelocityUpdateS2CPacket) packet).setX((int) (packet.getVelocityX() * h));
					((IEntityVelocityUpdateS2CPacket) packet).setY((int) (packet.getVelocityY() * v));
					((IEntityVelocityUpdateS2CPacket) packet).setZ((int) (packet.getVelocityZ() * h));
				}
			}
		}
	}
}