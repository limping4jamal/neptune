package me.neptune.mixin;

import me.neptune.Neptune;
import me.neptune.gui.ClickUI;
import me.neptune.utils.Wrapper;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Keyboard.class)
public class MixinKeyboard implements Wrapper {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (mc.currentScreen instanceof ClickUI && action == 1 && Neptune.MODULE.setBind(key)) {
            return;
        }
        if (mc.currentScreen != null) return;
        if (action == 1) {
            Neptune.MODULE.onKeyPressed(key);
        }
        if (action == 0) {
            //Neptune.moduleManager.onKeyReleased(key);
        }
    }
}
