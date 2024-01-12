package me.neptune.mixin;

import me.neptune.module.modules.render.Fullbright;
import me.neptune.module.modules.render.NoRender;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
	private void update(Args args) {
		if (Fullbright.INSTANCE.isOn()) {
			args.set(2, 0xFFFFFFFF);
		}
	}

	@Inject(method = "getDarknessFactor(F)F", at = @At("HEAD"), cancellable = true)
	private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
		if (NoRender.INSTANCE.isOn() && NoRender.INSTANCE.darkness.getValue()) info.setReturnValue(0.0f);
	}
}
