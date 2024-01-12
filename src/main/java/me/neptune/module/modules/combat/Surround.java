package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.utils.BlockUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Surround extends Module {

    /**
     * ill make better surround later! im busy now!!!
     */

    private final BooleanSetting rotate = new BooleanSetting("Rotate", "surround_rotate",true);
    public Surround() {
        super("Surround", Category.Combat);

        this.addSetting(rotate);
    }

    @Override
    public void onUpdate() {
        BlockPos pos = mc.player.getBlockPos();
        BlockUtil.placeBlock(pos.north(), true);
        BlockUtil.placeBlock(pos.east(), true);
        BlockUtil.placeBlock(pos.west(), true);
        BlockUtil.placeBlock(pos.south(), true);
    }
}
