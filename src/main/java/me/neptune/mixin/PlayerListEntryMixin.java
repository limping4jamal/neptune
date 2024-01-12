package me.neptune.mixin;

import me.neptune.module.modules.client.Cape;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    private static final Identifier IDENTIFIERUWU = new Identifier("modid", "uwucape.png");
    private static final Identifier IDENTIFIERMASK = new Identifier("modid", "maskcape.png");

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (!Cape.INSTANCE.isOn()) return;

        SkinTextures oldTextures = cir.getReturnValue();
        Identifier capeTexture;

        if (Cape.cape.getValue() == Cape.CapeMode.UWU) {
            capeTexture = IDENTIFIERUWU;
            SkinTextures Textures = new SkinTextures(oldTextures.texture(), oldTextures.textureUrl(), capeTexture, capeTexture, oldTextures.model(), oldTextures.secure());
            cir.setReturnValue(Textures);
        }
        if (Cape.cape.getValue() == Cape.CapeMode.MASK) {
            capeTexture = IDENTIFIERMASK;
            SkinTextures Textures = new SkinTextures(oldTextures.texture(), oldTextures.textureUrl(), capeTexture, capeTexture, oldTextures.model(), oldTextures.secure());
            cir.setReturnValue(Textures);
        }
    }
}