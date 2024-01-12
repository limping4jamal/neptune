package me.neptune.module.modules.movement;

import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class FastFall extends Module {
    private final SliderSetting speed = addSetting(new SliderSetting("Speed", "speed", 10f, 1f, 15f, 0.1));
    private final SliderSetting height = addSetting(new SliderSetting("Height", "height", 10f, 1f, 30f, 1.0));

    public FastFall() {
        super("FastFall", Category.Movement);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(mc.player == null || mc.world == null || !mc.player.isOnGround()) {
            return;
        }

        int blocks = traceDown();

        if(blocks <= height.getValueInt()) {
            mc.player.addVelocity(0.0, -speed.getValue(), 0.0);
        }
    }

    private int traceDown() {
        int blocks = 0;
        int y = (int) Math.round(mc.player.getY()) - 1;

        while(y >= 0) {
            Vec3d start = mc.player.getPos();
            Vec3d end = start.withAxis(Direction.Axis.Y, y);
            HitResult result = mc.world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            BlockPos pos = BlockPos.ofFloored(end);

            if(result != null && result.getType() == HitResult.Type.BLOCK) {
                return blocks;
            }

            blocks++;
            y--;
        }

        return blocks;
    }
}