package me.neptune.module.modules.combat;

import me.neptune.cmd.CommandManager;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.BlockUtil;
import me.neptune.utils.CombatUtil;
import me.neptune.utils.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

public class HolePush extends Module {
    /*private final EnumSetting method =
            new EnumSetting("Method", "holepush_method", Method.Block);

     */
    private final SliderSetting pistonDelay =
            new SliderSetting("PistonDelay", "holepush_pistondelay", 0, 0, 10, 1);
    private final SliderSetting activeDelay =
            new SliderSetting("ActiveDelay", "holepush_activedelay", 0, 0, 10, 1);
    private final SliderSetting range =
            new SliderSetting("Range","holepush_range", 3f, 2f, 10f, 0.1f);
    private final BooleanSetting rotate =
            new BooleanSetting("Rotate", "holepush_rotate",true);
    public HolePush() {
        super("HolePush", Category.Combat);
        this.setDescription("development");

        //addSetting(method);
        addSetting(pistonDelay);
        addSetting(activeDelay);
        addSetting(range);
        addSetting(rotate);
    }
    private BlockPos pistonPos, redstonePos;
    a stage;

    @Override
    public void onEnable() {
        stage = a.Piston;
        pistonPos = null;
        redstonePos = null;
    }

    @Override
    public void onUpdate() {
        int piston;
        int redstone;
        int old = mc.player.getInventory().selectedSlot;

        if ((piston = InventoryUtil.findBlock(Blocks.PISTON)) == -1) {
            return;
        }
        if ((redstone = InventoryUtil.findBlock(Blocks.REDSTONE_BLOCK)) == -1) {
            return;
        }

        for (PlayerEntity player : CombatUtil.getEnemies(range.getValue())) {
            switch (stage) {
                case Piston -> {
                    pistonPos = canPlacePiston(player.getBlockPos());

                    InventoryUtil.doSwap(piston);

                    BlockUtil.placeBlock(pistonPos, rotate.getValue());
                    stage = a.Red;
                }
                case Red -> {
                    redstonePos = canPlaceRedstone(pistonPos);

                    InventoryUtil.doSwap(redstone);

                    BlockUtil.placeBlock(redstonePos, rotate.getValue());

                    InventoryUtil.doSwap(old);

                    toggle();
                    CommandManager.sendChatMessage("Pushed Player");
                }
            }
        }
    }

    public static boolean canPlace(BlockPos pos) {
        if (pos == null) return false;
        if (!World.isValid(pos)) return false;
        if (!mc.world.getBlockState(pos).isReplaceable()) return false;

        return true;
    }

    public BlockPos canPlacePiston(BlockPos positions) {
        BlockPos p = positions.up(1);
        if (canPlace(p.add(-1,0,0))) return p.add(-1,0,0);
        else if (canPlace(p.add(0,0,1))) return  p.add(0,0,1);
        else if (canPlace(p.add(1,0,0))) return  p.add(1,0,0);
        else if (canPlace(p.add(0,0,-1))) return  p.add(0,0,-1);

        return null;
    }

    private BlockPos canPlaceRedstone(BlockPos pos) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        if(pos == null) return null;

        for(Direction dir : Direction.values()) if(canPlace(pos.offset(dir))) positions.add(pos.offset(dir));

        if(positions.isEmpty()) return null;

        return positions.get(0);
    }

    /**
     * bro, my friend said canplace method is "trash" ;-;
     */

    /*public enum Method {
        Block
        Torch
    }

     */

    public enum a {
        Piston,
        Red
    }
}
