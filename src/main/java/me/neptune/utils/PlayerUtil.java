package me.neptune.utils;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil implements Wrapper {

    public static Vec3d getPos;

    public static int getWorldActionId(ClientWorld world) {
        PendingUpdateManager pum = BlockUtil.getUpdateManager(Wrapper.mc.world);
        int p = pum.getSequence();
        pum.close();
        return p;
    }
    public static BlockPos getPlayerPos() {
        return new BlockPos((int) Wrapper.mc.player.getX(), (int) Wrapper.mc.player.getY(), (int) Wrapper.mc.player.getZ());
    }

}
