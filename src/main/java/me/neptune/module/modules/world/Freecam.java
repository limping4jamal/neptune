package me.neptune.module.modules.world;

import me.neptune.module.Module;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;

import java.lang.reflect.Field;

public class Freecam extends Module {
    private static Freecam INSTANCE;

    public Freecam(){
        super("Freecam",Category.World);
        INSTANCE = this;
        try {
            for (Field field : Freecam.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType()))
                    continue;
                Setting setting = (Setting) field.get(this);
                addSetting(setting);
            }
        } catch (Exception e) {
        }
    }
    private final SliderSetting speed =
            new SliderSetting("Speed", "freecam_speed",5.0, 0.0, 10.0, 0.1);
    @Override
    public void onEnable() {

    }
}
