package me.neptune.mixin;

import me.neptune.Neptune;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.neptune.utils.Wrapper.mc;

@Mixin(MinecraftClient.class)
public class ScreenTitleMixin {
    @Inject(method = "getWindowTitle",at = @At("HEAD"),cancellable = true)
    private void gatWindowTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(String.format(Neptune.NAME+" "+ Neptune.VERSION+" | Welcome to Neptune! | User: " + mc.getSession().getUsername()));

    }
}
