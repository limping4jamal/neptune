package me.neptune.mixin;

import me.neptune.module.modules.render.HandModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = {"getHandSwingDuration"}, at = {@At("HEAD")}, cancellable = true)
    private void getHandSwingDuration(final CallbackInfoReturnable<Integer> info) {
        if (HandModifier.INSTANCE.isOn() && HandModifier.shouldSlow.getValue())
            info.setReturnValue(HandModifier.slow.getValueInt());
    }
}
