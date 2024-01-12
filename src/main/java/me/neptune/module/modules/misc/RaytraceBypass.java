package me.neptune.module.modules.misc;

import me.neptune.mixin.AccessorPlayerMoveC2SPacket;
import me.neptune.module.Module;
import me.neptune.utils.Timer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class RaytraceBypass extends Module {
    private final Timer timer = new Timer();

    private float pitch = -91;

    public RaytraceBypass() {
        super("RaytraceBypass", Category.Misc);
        setDescription("skidded from mio client ok?");
    }

    @Override
    public void onSendPacket(Packet<?> packet) {
        super.onSendPacket(packet);

        if(packet instanceof PlayerInteractBlockC2SPacket && timer.passedMs(250)) {
            if(mc.world.isSpaceEmpty(mc.player.getBoundingBox().stretch(0, .15, 0))) {
                pitch = -75;
                timer.reset();
            }
        }

        if(packet instanceof PlayerMoveC2SPacket movePacket && pitch != -91) {
            ((AccessorPlayerMoveC2SPacket) movePacket).setPitch(pitch);
            pitch = -91;
        }
    }
}
