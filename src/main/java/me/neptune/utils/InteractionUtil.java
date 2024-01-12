package me.neptune.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionUtil implements Wrapper {

    private static Map<BlockPos, Long> awaiting = new HashMap<>();

    public static boolean placeBlock(BlockPos bp, boolean rotate, Interact interact, PlaceMode mode, boolean ignoreEntities) {
        if (mc.world == null) return false;

        BlockHitResult result = getPlaceResult(bp, interact, ignoreEntities);
        if (result == null) return false;

        boolean sprint = mc.player.isSprinting();
        boolean sneak = needSneak(mc.world.getBlockState(result.getBlockPos()).getBlock()) && !mc.player.isSneaking();

        if (sprint) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        if (sneak) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }

        float[] angle = RotationUtil.calculateAngle(result.getPos());

        if (rotate) {
            mc.player.networkHandler.sendPacket(new LookAndOnGround(angle[0], angle[1], mc.player.isOnGround()));
        }

        if (mode == PlaceMode.Normal) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
        }

        if (mode == PlaceMode.Packet) {
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, PlayerUtil.getWorldActionId(mc.world)));
        }

        awaiting.put(bp, System.currentTimeMillis());

        if (sneak) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }

        if (sprint) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }

        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        return true;
    }

    public static boolean canPlaceBlock(BlockPos bp, Interact interact, boolean ignoreEntities) {
        return !awaiting.containsKey(bp) && getPlaceResult(bp, interact, ignoreEntities) != null;
    }

    public static BlockHitResult getPlaceResult(BlockPos bp, Interact interact, boolean ignoreEntities) {
        if (!ignoreEntities) {
            List<Entity> cache = new ArrayList<>(mc.world.getNonSpectatingEntities(Entity.class, new Box(bp)));

            for (Entity entity : cache) {
                if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity)) {
                    return null;
                }
            }
        }

        if (!mc.world.getBlockState(bp).isReplaceable()) {
            return null;
        }

        List<BlockPosWithFacing> supports = getSupportBlocks(bp);

        for (BlockPosWithFacing support : supports) {
        if (interact != Interact.Vanilla) {
            if (RotationUtil.getStrictDirections(bp).isEmpty()) {
                return null;
            }

            if (!RotationUtil.getStrictDirections(bp).contains(support.getFacing())) {
                continue;
            }
        }

        BlockHitResult result = null;

        if (interact != Interact.Legit) {
            Vec3d directionVec = new Vec3d(support.getPosition().getX() + 0.5 + support.getFacing().getVector().getX() * 0.5,
                support.getPosition().getY() + 0.5 + support.getFacing().getVector().getY() * 0.5,
                support.getPosition().getZ() + 0.5 + support.getFacing().getVector().getZ() * 0.5);

            result = new BlockHitResult(directionVec, support.getFacing(), support.getPosition(), false);
        } else {
            Vec3d p = getVisibleDirectionPoint(support.getFacing(), support.getPosition());

            if (p != null) {
                return new BlockHitResult(p, support.getFacing(), support.getPosition(), false);
            }
        }

        return result;
    }

        return null;
    }

    @SuppressWarnings("DEPRECATION")
    public static ArrayList<BlockPosWithFacing> getSupportBlocks(BlockPos bp) {
        ArrayList<BlockPosWithFacing> list = new ArrayList<>();

        if (mc.world.getBlockState(bp.down()).isSolid() || awaiting.containsKey(bp.down())) {
            list.add(new BlockPosWithFacing(bp.down(), Direction.UP));
        }

        if (mc.world.getBlockState(bp.up()).isSolid() || awaiting.containsKey(bp.up())) {
            list.add(new BlockPosWithFacing(bp.up(), Direction.DOWN));
        }

        if (mc.world.getBlockState(bp.east()).isSolid() || awaiting.containsKey(bp.east())) {
            list.add(new BlockPosWithFacing(bp.east(), Direction.WEST));
        }

        if (mc.world.getBlockState(bp.west()).isSolid() || awaiting.containsKey(bp.west())) {
            list.add(new BlockPosWithFacing(bp.west(), Direction.EAST));
        }

        if (mc.world.getBlockState(bp.north()).isSolid() || awaiting.containsKey(bp.north())) {
            list.add(new BlockPosWithFacing(bp.north(), Direction.SOUTH));
        }

        if (mc.world.getBlockState(bp.south()).isSolid() || awaiting.containsKey(bp.south())) {
            list.add(new BlockPosWithFacing(bp.south(), Direction.NORTH));
        }

        return list;
    }

    @SuppressWarnings("DEPRECATION")
    public static BlockPosWithFacing checkNearBlocks(BlockPos blockPos) {
        if (mc.world.getBlockState(blockPos.down()).isSolid()) {
            return new BlockPosWithFacing(blockPos.down(), Direction.UP);
        } else if (mc.world.getBlockState(blockPos.east()).isSolid()) {
            return new BlockPosWithFacing(blockPos.east(), Direction.EAST);
        } else if (mc.world.getBlockState(blockPos.west()).isSolid()) {
            return new BlockPosWithFacing(blockPos.west(), Direction.WEST);
        } else if (mc.world.getBlockState(blockPos.north()).isSolid()) {
            return new BlockPosWithFacing(blockPos.north(), Direction.NORTH);
        } else if (mc.world.getBlockState(blockPos.south()).isSolid()) {
            return new BlockPosWithFacing(blockPos.south(), Direction.SOUTH);
        }

        return null;
    }

    public static class BlockPosWithFacing {
        private final BlockPos position;
        private final Direction facing;

        public BlockPosWithFacing(BlockPos position, Direction facing) {
            this.position = position;
            this.facing = facing;
        }

        public BlockPos getPosition() {
            return position;
        }

        public Direction getFacing() {
            return facing;
        }
    }

    private static final List<Block> shiftBlocks = List.of(
            Blocks.ENDER_CHEST,
    Blocks.CHEST,
    Blocks.TRAPPED_CHEST,
    Blocks.CRAFTING_TABLE,
    Blocks.BIRCH_TRAPDOOR,
    Blocks.BAMBOO_TRAPDOOR,
    Blocks.DARK_OAK_TRAPDOOR,
    Blocks.CHERRY_TRAPDOOR,
    Blocks.ANVIL,
    Blocks.BREWING_STAND,
    Blocks.HOPPER,
    Blocks.DROPPER,
    Blocks.DISPENSER,
    Blocks.ACACIA_TRAPDOOR,
    Blocks.ENCHANTING_TABLE,
    Blocks.WHITE_SHULKER_BOX,
    Blocks.ORANGE_SHULKER_BOX,
    Blocks.MAGENTA_SHULKER_BOX,
    Blocks.LIGHT_BLUE_SHULKER_BOX,
    Blocks.YELLOW_SHULKER_BOX,
    Blocks.LIME_SHULKER_BOX,
    Blocks.PINK_SHULKER_BOX,
    Blocks.GRAY_SHULKER_BOX,
    Blocks.CYAN_SHULKER_BOX,
    Blocks.PURPLE_SHULKER_BOX,
    Blocks.BLUE_SHULKER_BOX,
    Blocks.BROWN_SHULKER_BOX,
    Blocks.GREEN_SHULKER_BOX,
    Blocks.RED_SHULKER_BOX,
    Blocks.BLACK_SHULKER_BOX
    );

    @SuppressWarnings("unreachable")
    public static Vec3d getVisibleDirectionPoint(Direction dir, BlockPos bp) {
        Box brutBox = switch (dir) {
            case DOWN -> new Box(new Vec3d(0.1, 0.0, 0.1), new Vec3d(0.9, 0.0, 0.9));
            case NORTH -> new Box(new Vec3d(0.1, 0.1, 0.0), new Vec3d(0.9, 0.9, 0.0));
            case EAST -> new Box(new Vec3d(1.0, 0.1, 0.1), new Vec3d(1.0, 0.9, 0.9));
            case SOUTH -> new Box(new Vec3d(0.1, 0.1, 1.0), new Vec3d(0.9, 0.9, 1.0));
            case WEST -> new Box(new Vec3d(0.0, 0.1, 0.1), new Vec3d(0.0, 0.9, 0.9));
            case UP -> new Box(new Vec3d(0.1, 1.0, 0.1), new Vec3d(0.9, 1.0, 0.9));
        };

        if (brutBox.maxX - brutBox.minX == 0.0) {
            double y = brutBox.minY;

            while (y < brutBox.maxY) {
                double z = brutBox.minZ;

                while (z < brutBox.maxZ) {
                    Vec3d point = new Vec3d(bp.getX() + brutBox.minX, bp.getY() + y, bp.getZ() + z);
                    HitResult wallCheck = mc.world.raycast(new RaycastContext(RotationUtil.getEyesPos(mc.player),
                    point, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, mc.player));

                    if (wallCheck != null && wallCheck.getType() == HitResult.Type.BLOCK && !((BlockHitResult) wallCheck).getBlockPos().equals(bp)) {
                        z += 0.1;
                        continue;
                    }

                    return point;
                }
                y += 0.1;
            }
        }

        if (brutBox.maxY - brutBox.minY == 0.0) {
            double x = brutBox.minX;

            while (x < brutBox.maxX) {
                double z = brutBox.minZ;

                while (z < brutBox.maxZ) {
                    Vec3d point = new Vec3d(bp.getX() + x, bp.getY() + brutBox.minY, bp.getZ() + z);
                    HitResult wallCheck = mc.world.raycast(new RaycastContext(RotationUtil.getEyesPos(mc.player),
                    point, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, mc.player));

                    if (wallCheck != null && wallCheck.getType() == HitResult.Type.BLOCK && !((BlockHitResult) wallCheck).getBlockPos().equals(bp)) {
                        z += 0.1;
                        continue;
                    }

                    return point;
                }
                x += 0.1;
            }
        }

        if (brutBox.maxZ - brutBox.minZ == 0.0) {
            double x = brutBox.minX;

            while (x < brutBox.maxX) {
                double y = brutBox.minY;

                while (y < brutBox.maxY) {
                    Vec3d point = new Vec3d(bp.getX() + x, bp.getY() + y, bp.getZ() + brutBox.minZ);
                    HitResult wallCheck = mc.world.raycast(new RaycastContext(RotationUtil.getEyesPos(mc.player),
                    point, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, mc.player));

                    if (wallCheck != null && wallCheck.getType() == HitResult.Type.BLOCK && !((BlockHitResult) wallCheck).getBlockPos().equals(bp)) {
                        y += 0.1;
                        continue;
                    }

                    return point;
                }
                x += 0.1;
            }
        }

        return null;
    }

    public static boolean needSneak(Block b) {
        return shiftBlocks.contains(b);
    }

    public enum PlaceMode {
            Packet,
            Normal
    }

    public enum Interact {
            Vanilla,
            Strict,
            Legit
    }
}
