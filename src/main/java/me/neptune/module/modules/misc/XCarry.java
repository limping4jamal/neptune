package me.neptune.module.modules.misc;

import me.neptune.events.impl.PacketEvent;
import me.neptune.module.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module {

    public XCarry() {
        super("XCarry", Category.Misc);
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
            if (mc.currentScreen instanceof InventoryScreen) {
                event.setCancel(true);
            }
        }
    }
}
