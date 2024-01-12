package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.BlockUtil;
import me.neptune.utils.CombatUtil;
import me.neptune.utils.InventoryUtil;
import me.neptune.utils.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BedAura extends Module {


    private final SliderSetting range = new SliderSetting("Range", "ba_range", 4.0f, 1.0f, 6.0f, 0.1);
    private final SliderSetting delay = new SliderSetting("Delay", "ba_delay", 2.0f, 1.0f, 10.0f, 0.1);
    private final SliderSetting minDmg = new SliderSetting("MinDmg", "ba_mindmg", 5.4f, 1.0f, 36.0f, 0.1);
    private final SliderSetting maxDmg = new SliderSetting("MaxDmg", "ba_maxdmg", 3.8f, 1.0f, 36.0f, 0.1);

    public BedAura() {
        super("BedAura", Category.Combat);
        this.setDescription("Automatically places and explodes beds.");
        try {
            for (Field field : BedAura.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType())) continue;
                Setting setting = (Setting) field.get(this);
                addSetting(setting);
            }
        } catch (Exception ignored) {}
    }

    private PlayerEntity target = null;
    private BedPos pos = null;
    @Override
    public void onUpdate() {
        if (target == null) target = CombatUtil.getEnemies(range.getValueFloat()).get(0); // find target
        if (target == null) return;

        if (pos == null) pos = getPlacePos(target); // find pos
        if (pos == null) return;

        if (!pos.placed) { // place bed
            pos.place();
            pos.tick();
            return;
        }

        if (pos.ticksPlaced < delay.getValueInt()) { // wait to break
            pos.tick();
            return;
        }

        if (!pos.exploded) { // break
            pos.explode();
            pos = null;
        }

    }

    // Find the first valid placement around the target
    private BedPos getPlacePos(PlayerEntity target) {
        for (BlockPos pos : BlockUtil.getSphere(range.getValueFloat())) {
            if (BlockUtil.canPlace(pos, range.getValueFloat())) {
                Direction dir = getPlaceDirection(pos);
                Vec3d exp = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                if (dir != null && CombatUtil.getDamage(exp, target) >= minDmg.getValueFloat()) {
                    if (CombatUtil.getDamage(exp, mc.player) <= maxDmg.getValueFloat()) return new BedPos(pos, dir);
                }
            }
        }
        return null;
    }

    // Find the correct orientation to place the bed
    private Direction getPlaceDirection(BlockPos pos) {
        if (BlockUtil.canPlace(pos.offset(Direction.NORTH), range.getValueFloat())) return Direction.NORTH;
        else if (BlockUtil.canPlace(pos.offset(Direction.SOUTH), range.getValueFloat())) return Direction.SOUTH;
        else if (BlockUtil.canPlace(pos.offset(Direction.EAST), range.getValueFloat())) return Direction.EAST;
        else if (BlockUtil.canPlace(pos.offset(Direction.WEST), range.getValueFloat())) return Direction.WEST;
        else return null;
    }

    private static class BedPos {
        private BlockPos pos;
        private Direction dir;

        private boolean placed;
        private boolean exploded;

        private int ticksPlaced;

        public BedPos(BlockPos pos, Direction dir) {
            this.pos = pos;
            this.dir = dir;
            placed = false;
            exploded = false;
            ticksPlaced = 0;
        }

        public void place() { // place the bed
            int bedSlot = InventoryUtil.findClass(BedItem.class);
            if (bedSlot == -1) return;

            InventoryUtil.doSwap(bedSlot);
            BlockPos rotatePos = pos.offset(dir);
            RotationUtil.lookAt(rotatePos);
            BlockUtil.placeBlock(pos, true);
            placed = true;
        }

        public void explode() { // explode the bed
            boolean sneak = mc.player.isSneaking();
            if (sneak) mc.player.setSneaking(false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, BlockUtil.genericHitResult(pos));
            mc.player.setSneaking(sneak);
            exploded = true;
        }

        public void tick() {
            if (!placed) return;
            ticksPlaced++;
        }
    }
}
