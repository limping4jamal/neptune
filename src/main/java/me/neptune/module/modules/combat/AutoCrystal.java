package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.EnumSetting;
import me.neptune.settings.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static me.neptune.module.modules.combat.AutoCrystal.HandMode.MAIN_HAND;
import static me.neptune.module.modules.combat.AutoCrystal.HandMode.OFF_HAND;

/**
 * @author Thomas
 **/

public class AutoCrystal extends Module {

    private final BooleanSetting breakRotate = new BooleanSetting("Break Rotate", "break_rotate",true);
    private final BooleanSetting placeRotate = new BooleanSetting("Place Rotate", "place_rotate",true);
    private final BooleanSetting setDead = new BooleanSetting("Set Dead", "set_dead",true);
    private final EnumSetting handMode = new EnumSetting("Hand Mode", MAIN_HAND);
    private final SliderSetting findTargetRange = new SliderSetting("Find Target Range", "range_target", 6.0f, 0.0f, 6.0f, 0.1);

    private final SliderSetting breakRange = new SliderSetting("Break Crystal Range", "range_break", 7.0f, 0.0f, 10.0f, 0.1);


    public EndCrystalEntity crystalEntity;
    public int ticks;
    public AutoCrystal() {
        super("AutoCrystal", Category.Combat);
        this.setDescription("Automatically places crystals and destroys them.");
        this.addSetting(findTargetRange);
        this.addSetting(setDead);
        this.addSetting(handMode);
        this.addSetting(breakRange);
        this.addSetting(breakRotate);
        this.addSetting(placeRotate);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        crystalEntity = null;
        ticks = 0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (mc.player == null) return;
        // Checks for a possible End Crystal in range and Breaks it.
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                if (mc.player.distanceTo(entity) > breakRange.getValueFloat())
                    return; // Checks if the range between the player and EndCrystal is more than 6 blocks
                crystalEntity = (EndCrystalEntity) entity;
                if (setDead.getValue()) entity.extinguish(); // Doesn't Render the EndCrystal being broken.
                // Breaks the EndCrystal
                mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, false));
            }
        }

        // Finds nearest target
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player) {
                if (mc.player.distanceTo(player) > findTargetRange.getValueFloat())
                    return; // Checks if the target is in range

                // Places (For now) a end crystal infront of a player's X value (No targetting / Crystal Calculations yet))
                if (mc.world.getBlockState(new BlockPos((int) (player.getX() + 1), (int) (player.getY() - 1), (int) player.getZ())).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(new BlockPos((int) (player.getX() + 1), (int) (player.getY() - 1), (int) player.getZ())).getBlock().equals(Blocks.BEDROCK)) {
                    if (handMode.getValue().equals(MAIN_HAND)) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    } else if (handMode.getValue().equals(OFF_HAND)) {
                        mc.player.swingHand(Hand.OFF_HAND);
                    }
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(player.getX() + 1, player.getY(), player.getZ()), Direction.DOWN, new BlockPos((int) (player.getX() + 1), (int) (player.getY() - 1), (int) player.getZ()), true));
                } else return;
            }
        }
    }

    public enum HandMode {
        MAIN_HAND,
        OFF_HAND
    }

}
