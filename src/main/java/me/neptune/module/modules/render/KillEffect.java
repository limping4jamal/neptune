package me.neptune.module.modules.render;

import me.neptune.module.Module;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KillEffect extends Module {

    private final Map<Entity, Long> renderEntities = new ConcurrentHashMap<Entity, Long>();
    private final Map<Entity, Long> lightingEntities = new ConcurrentHashMap<Entity, Long>();

    public KillEffect() {
        super("KillEffect", Category.Render);
    }

    @Override
    public void onUpdate() {
        this.mc.world.getEntities().forEach(entity -> {
            if (entity == this.mc.player || this.renderEntities.containsKey(entity) || this.lightingEntities.containsKey(entity) ) {
                return;
            }
            if (entity.isAlive()) {
                return;
            }
            if(!(entity instanceof PlayerEntity)) {
                return;
            }
            this.renderEntities.put((Entity) entity, System.currentTimeMillis());
        });
        if (!this.lightingEntities.isEmpty()) {
            this.lightingEntities.forEach((entity, time) -> {
                if (System.currentTimeMillis() - time > 5000L) {
                    this.lightingEntities.remove(entity);
                }
            });
        }
    }

    @Override
    public void onRender(MatrixStack matrixStack, float partialTicks) {
        if (this.mc.world == null) {
            return;
        }
        this.renderEntities.forEach((entity, time) -> {
            Entity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, this.mc.world);
            lightningEntity.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
            EntitySpawnS2CPacket pac = new EntitySpawnS2CPacket(lightningEntity);
            pac.apply(this.mc.getNetworkHandler());
            this.renderEntities.remove(entity);
            this.lightingEntities.put((Entity) entity, System.currentTimeMillis());
        });
    }
}
