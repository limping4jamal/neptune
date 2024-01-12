package me.neptune.module.modules.client;

import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;

import java.lang.reflect.Field;

public class Test extends Module {
    public static Test INSTANCE;
    public Test() {
        super("Test", Category.Client);
        INSTANCE = this;
        try {
            for (Field field : Test.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType()))
                    continue;
                Setting setting = (Setting) field.get(this);
                addSetting(setting);
            }
        } catch (Exception e) {
        }
    }
    private final BooleanSetting Test =
            new BooleanSetting("Spam", "1",false);
    private final SliderSetting range =
            new SliderSetting("Range", "2",5.0, 0.0, 6.0, 0.1);


}
