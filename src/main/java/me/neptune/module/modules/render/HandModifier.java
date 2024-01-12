package me.neptune.module.modules.render;

import me.neptune.events.impl.PacketEvent;
import me.neptune.mixin.IHeldItemRenderer;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.SliderSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;

public class HandModifier extends Module {

    /**
     * @description Forked in MixinLivingEntity and MixinHeldItemRenderer, IHeldItemRenderer
     */
    public static HandModifier INSTANCE;
    public static final BooleanSetting shouldScale = new BooleanSetting("scale", "hand_scalemain", false);
    public static final SliderSetting scale = new SliderSetting("HandScale", "hand_scale", 1, 0.1, 1.2, 0.1);
    public static final BooleanSetting shouldSlow = new BooleanSetting("slow", "hand_slow", false);
    public static final SliderSetting slow = new SliderSetting("Speed", "hand_slowswing", 6, 1.0, 18.0, 0.1);
    public static final BooleanSetting old = new BooleanSetting("OldAnimations", "hand_old", true);
    public static final BooleanSetting noSwing = new BooleanSetting("NoSwing", "hand_noswing", false);

    public HandModifier() {
        super("HandModifier", Category.Render);

        super.addSetting(shouldScale);
        super.addSetting(scale);
        super.addSetting(slow);
        super.addSetting(shouldSlow);
        super.addSetting(old);
        super.addSetting(noSwing);
        INSTANCE = this;
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (noSwing.getValue() && event.getPacket() instanceof HandSwingC2SPacket) mc.player.handSwinging = false;
    }

    @EventHandler
    private void onPacketRecieve(PacketEvent.Receive event) {
        if (noSwing.getValue() && event.getPacket() instanceof HandSwingC2SPacket) mc.player.handSwinging = false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        if (old.getValue() && ((IHeldItemRenderer) mc.getEntityRenderDispatcher().getHeldItemRenderer()).getEquippedProgressMainHand() <= 1f) {
            ((IHeldItemRenderer) mc.getEntityRenderDispatcher().getHeldItemRenderer()).setEquippedProgressMainHand(1f);
            ((IHeldItemRenderer) mc.getEntityRenderDispatcher().getHeldItemRenderer()).setItemStackMainHand(mc.player.getMainHandStack());
        }
        if (old.getValue() && ((IHeldItemRenderer) mc.getEntityRenderDispatcher().getHeldItemRenderer()).getEquippedProgressOffHand() <= 1) {
            ((IHeldItemRenderer) mc.getEntityRenderDispatcher().getHeldItemRenderer()).setEquippedProgressOffHand(1f);
            ((IHeldItemRenderer) mc.getEntityRenderDispatcher().getHeldItemRenderer()).setItemStackOffHand(mc.player.getOffHandStack());
        }
    }
}
