package me.neptune.events.impl;

import me.neptune.events.Event;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends Event {

    public AttackEntityEvent(){
        super(Stage.Pre);
    }
    private static final AttackEntityEvent INSTANCE = new AttackEntityEvent();

    public Entity entity;

    public static AttackEntityEvent get(Entity entity) {
        INSTANCE.setCancel(false);
        INSTANCE.entity = entity;
        return INSTANCE;
    }
}
