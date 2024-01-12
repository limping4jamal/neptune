package me.neptune.module.modules.client;

import me.neptune.events.impl.PacketEvent;
import me.neptune.module.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.Objects;

public class ChatSuffix extends Module {
    private final String SUFFIX = " | Neptune";

    public ChatSuffix() {
        super("ChatSuffix", Category.Client);
    }
    private String string;

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof ChatMessageC2SPacket packet) {
            if (packet.chatMessage().startsWith("/") || packet.chatMessage().startsWith("+")) {
                return;
            }
            if (Objects.equals(packet.chatMessage(), string)) {
                return;
            }

            string = packet.chatMessage() + SUFFIX;
            mc.player.networkHandler.sendChatMessage(packet.chatMessage() + SUFFIX);
            event.setCancel(true);
        }
    }
}
