package me.neptune.module.modules.misc;

import me.neptune.mixin.IEntity;
import me.neptune.module.Module;

public class PortalChat extends Module {

    public PortalChat() {
        super("PortalChat", Category.Misc);
    }

    @Override
    public void onUpdate() {
        ((IEntity)mc.player).setInNetherPortal(false);
    }
}
