package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.utils.BlockUtil;
import me.neptune.utils.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.BlockPos;

public class Blocker extends Module {
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "blocker_rotate",true);
    // ???

    public Blocker() {
        super("Blocker", Category.Combat);
        this.setDescription("development");

        this.addSetting(rotate);
    }
    a stage;

    @Override
    public void onEnable() {
        stage = a.Break;
    }

    @Override
    public void onUpdate() {
        int obsidian;
        int old = mc.player.getInventory().selectedSlot;

        BlockPos pos = mc.player.getBlockPos();

        if ((obsidian = InventoryUtil.findBlock(Blocks.OBSIDIAN)) == -1) {
        }

        for (Entity entity : mc.world.getEntities()) {
            switch (stage) {
                case Break -> {
                    if (entity instanceof EndCrystalEntity && entity.getBlockPos().equals(pos.up(2).up())
                    || entity.getBlockPos().equals(pos.up(2).up().south(1))
                    || entity.getBlockPos().equals(pos.up(2).up().west(1))
                    || entity.getBlockPos().equals(pos.up(2).up().north(1))
                    || entity.getBlockPos().equals(pos.up(2).up().east(1))){
                        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
                        stage = a.Place;
                    }
                }
                case Place -> {
                    InventoryUtil.doSwap(obsidian);
                    BlockUtil.placeBlock(pos.up(2).up(), rotate.getValue());

                    InventoryUtil.doSwap(old);
                    stage = a.Break;
                }
            }
        }
    }

    public enum a {
        Break,
        Place
    }
}
