package me.neptune.module.modules.movement;

import me.neptune.events.impl.MoveEvent;
import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.MovementUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class BurrowStrafe extends Module {

    // dev module

    private final SliderSetting speed1 = new SliderSetting("speed", "burrow_strafe_speed", 20f, 0f, 120f, 0.1);

    private double speed;

    public BurrowStrafe() {
        super("BurrowStrafe", Category.Movement);
        this.setDescription("Slows Down Speed when u burrow");
        this.addSetting(speed1);
    }
    /*public void isBurrow(){
        if(mc.player.noClip){

        }
    }

     */

    @EventHandler
    public void invoke(MoveEvent event) {
        if (isBurrowed(mc.player) && MovementUtil.isMoving()) {
            double sprinting = MovementUtil.getMoveForward();
            double getSpeed = MovementUtil.getMoveStrafe();
            double yaw = mc.player.getYaw();

            this.speed = Math.max(this.speed, MovementUtil.getSpeed(false, this.speed1.getValue() / 1000));

            if (sprinting == 0.0 && getSpeed == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else if (sprinting != 0.0 && getSpeed != 0.0) {
                sprinting *= Math.sin(0.53789);
                getSpeed *= Math.cos(0.53789);
            }

            event.setX((sprinting * this.speed * -Math.sin(Math.toRadians(yaw)) + getSpeed * this.speed * Math.cos(Math.toRadians(yaw))));
            event.setZ((sprinting * this.speed * Math.cos(Math.toRadians(yaw)) - getSpeed * this.speed * -Math.sin(Math.toRadians(yaw))));
        }
    }

    public boolean isBurrowed(PlayerEntity player) {
        BlockPos pos = new BlockPos(player.getBlockPos().getX(), (int) (player.getBlockPos().getY() + 0.3), player.getBlockPos().getZ());
        return mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }
}