package me.neptune.module.modules.misc;

import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AirPlace extends Module {
    private final SliderSetting range = new SliderSetting("Range", "airplace_range", 6.0, 1.0, 6.0, 0.1);

    public AirPlace() {
        super("AirPlace", Category.Misc);
        super.setDescription("allows to block place to air on no ac servers");

        super.addSetting(range);
    }
    BlockHitResult hit;

    @Override
    public void onUpdate() {
        if (mc.player.isUsingItem()) return;
        HitResult hitResult = mc.getCameraEntity().raycast(range.getValue(), 0, false);

        if (hitResult instanceof BlockHitResult) hit = (BlockHitResult) hitResult;

        if (mc.options.useKey.isPressed() && mc.player.getMainHandStack().getItem() instanceof BlockItem) {
            if (mc.player.isSprinting()) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            if (!mc.player.isSneaking()) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        }
    }
}
