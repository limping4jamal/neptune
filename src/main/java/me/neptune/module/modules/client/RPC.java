/*package me.neptune.module.modules.client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.neptune.module.Module;

public class RPC extends Module {
    public static DiscordRichPresence presence;
    public static final DiscordRPC rpc;
    public static Thread thread;

    public RPC() {
        super("ClientRPC", Category.Client);
    }

    @Override
    public void onUpdate() {
        if (mc.player != null) start();
    }

    @Override
    public void onDisable() {
        stop();
    }

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("1192278741775290459", handlers, true, "");

        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.details = "Smoking the Opps";
        presence.state = "https://discord.gg/JZfrmfSQ6B";
        presence.largeImageKey = "logo";
        presence.largeImageText = "Buy Neptune At https://discord.gg/UhaJbFAeJD";

        rpc.Discord_UpdatePresence(presence);
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }

        rpc.Discord_Shutdown();
    }

    static {
        rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}*/
