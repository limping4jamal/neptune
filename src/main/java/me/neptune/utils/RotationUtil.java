package me.neptune.utils;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;

public class RotationUtil implements Wrapper {

    public static boolean canSee(Entity entity) {
        Vec3d entityEyes = getEyesPos(entity);
        Vec3d entityPos = entity.getPos();
        return canSee(entityEyes, entityPos);
    }

    public static boolean canSee(Vec3d entityEyes, Vec3d entityPos) {
        if (mc.player == null || mc.world == null) return false;
        Vec3d playerEyes = getEyesPos(mc.player);
        if (mc.world.raycast(new RaycastContext(playerEyes, entityEyes, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS) {
            return true;
        }
        return playerEyes.getY() > entityPos.getY() && mc.world.raycast(new RaycastContext(playerEyes, entityPos,
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;
    }

    public static Vec3d getEyesPos(Entity entity) {
        return entity.getPos().add(0.0, entity.getEyeHeight(entity.getPose()), 0.0);
    }

    public static float[] calculateAngle(Vec3d to) {
        return calculateAngle(getEyesPos(mc.player), to);
    }

    public static float[] calculateAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));
        float yD = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f);
        float pD = (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))), -90.0f, 90.0f);
        return new float[]{yD, pD};
    }

    public static float[] getPlaceAngle(BlockPos bp, InteractionUtil.Interact interact, boolean ignoreEntities) {
        BlockHitResult result = InteractionUtil.getPlaceResult(bp, interact, ignoreEntities);
        return (result != null) ? calculateAngle(result.getPos()) : null;
    }

    public static float squaredDistanceFromEyes(Vec3d vec) {
        double d0 = vec.x - mc.player.getX();
        double d1 = vec.z - mc.player.getZ();
        double d2 = vec.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        return (float) (d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static List<Direction> getStrictDirections(BlockPos bp) {
        List<Direction> visibleSides = new ArrayList<>();
        Vec3d positionVector = bp.toCenterPos();
        double westDelta = getEyesPos(mc.player).x - positionVector.add(0.5, 0.0, 0.0).x;
        double eastDelta = getEyesPos(mc.player).x - positionVector.add(-0.5, 0.0, 0.0).x;
        double northDelta = getEyesPos(mc.player).z - positionVector.add(0.0, 0.0, 0.5).z;
        double southDelta = getEyesPos(mc.player).z - positionVector.add(0.0, 0.0, -0.5).z;
        double upDelta = getEyesPos(mc.player).y - positionVector.add(0.0, 0.5, 0.0).y;
        double downDelta = getEyesPos(mc.player).y - positionVector.add(0.0, -0.5, 0.0).y;
        if (westDelta > 0 && !mc.world.getBlockState(bp.west()).isReplaceable()) visibleSides.add(Direction.EAST);
        if (westDelta < 0 && !mc.world.getBlockState(bp.east()).isReplaceable()) visibleSides.add(Direction.WEST);
        if (eastDelta < 0 && !mc.world.getBlockState(bp.east()).isReplaceable()) visibleSides.add(Direction.WEST);
        if (eastDelta > 0 && !mc.world.getBlockState(bp.west()).isReplaceable()) visibleSides.add(Direction.EAST);
        if (northDelta > 0 && !mc.world.getBlockState(bp.north()).isReplaceable()) visibleSides.add(Direction.SOUTH);
        if (northDelta < 0 && !mc.world.getBlockState(bp.south()).isReplaceable()) visibleSides.add(Direction.NORTH);
        if (southDelta < 0 && !mc.world.getBlockState(bp.south()).isReplaceable()) visibleSides.add(Direction.NORTH);
        if (southDelta > 0 && !mc.world.getBlockState(bp.north()).isReplaceable()) visibleSides.add(Direction.SOUTH);
        if (upDelta > 0 && !mc.world.getBlockState(bp.down()).isReplaceable()) visibleSides.add(Direction.UP);
        if (upDelta < 0 && !mc.world.getBlockState(bp.up()).isReplaceable()) visibleSides.add(Direction.DOWN);
        if (downDelta < 0 && !mc.world.getBlockState(bp.up()).isReplaceable()) visibleSides.add(Direction.DOWN);
        if (downDelta > 0 && !mc.world.getBlockState(bp.down()).isReplaceable()) visibleSides.add(Direction.UP);
        return visibleSides;
    }

    public static double getPitch(Vec3d pos) {
        double diffX = pos.getX() - mc.player.getX();
        double diffY = pos.getY() - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getPitch() + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.getPitch());
    }

    public static double getYaw(BlockPos pos) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() + 0.5 - mc.player.getZ(), pos.getX() + 0.5 - mc.player.getX())) - 90f - mc.player.getYaw());
    }

    public static void lookAt(BlockPos pos) {
        double yaw = getYaw(pos);
        double pitch = getPitch(BlockUtil.toVec3d(pos));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, mc.player.isOnGround()));
    }
}
