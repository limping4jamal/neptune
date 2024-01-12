package me.neptune.module.modules.client;

import me.neptune.module.Module;
import me.neptune.settings.EnumSetting;

public class Cape extends Module {
    public static final EnumSetting cape = new EnumSetting("Cape Mode", "cape_mode", CapeMode.UWU);
    public static Cape INSTANCE = new Cape();

    public Cape() {
        super("Cape", Category.Client);
        this.setDescription("Renders Cape");

        this.addSetting(cape);
        INSTANCE = this;
    }

    public enum CapeMode {
        UWU,
        MASK
    }
}
