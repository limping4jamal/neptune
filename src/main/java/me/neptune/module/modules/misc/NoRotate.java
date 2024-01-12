package me.neptune.module.modules.misc;

import me.neptune.events.impl.PacketEvent;
import me.neptune.mixin.IPlayerPositionLookS2CPacket;
import me.neptune.module.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", Category.Misc);
        this.setDescription("Cancel rotations sent from server");
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
            ((IPlayerPositionLookS2CPacket) packet).setPitch(mc.player.getPitch());
            ((IPlayerPositionLookS2CPacket) packet).setYaw(mc.player.getYaw());
        }
    }
}
