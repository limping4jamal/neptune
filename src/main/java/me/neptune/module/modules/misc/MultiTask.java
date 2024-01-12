package me.neptune.module.modules.misc;

import me.neptune.module.Module;

public class MultiTask extends Module {
    public static MultiTask INSTANCE;
    public MultiTask() {
        super("MultiTask", Category.Misc);
        this.setDescription("u can eat and break block as same time");
        INSTANCE = this;
    }
}
