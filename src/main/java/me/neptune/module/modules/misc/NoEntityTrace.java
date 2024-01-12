package me.neptune.module.modules.misc;

import me.neptune.module.Module;

public class NoEntityTrace extends Module {

    /**
     * @description Forked in MixinGameRenderer
     */
    public static NoEntityTrace INSTANCE;

    public NoEntityTrace() {
        super("NoEntityTrace", Category.Misc);

        INSTANCE = this;
    }
}
