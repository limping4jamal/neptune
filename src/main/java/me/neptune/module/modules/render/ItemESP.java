/**
 * ItemESP Module
 */
package me.neptune.module.modules.render;

import me.neptune.gui.Color;
import me.neptune.module.Module;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

public class ItemESP extends Module {

	public ItemESP() {
		super("ItemESP", Category.Render);
		this.setDescription("Allows the player to see items with an ESP.");

	}

    @Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		for (Entity entity : mc.world.getEntities()) {
			if(entity instanceof ItemEntity) {
				this.getRenderUtils().draw3DBox(matrixStack, entity.getBoundingBox(), new Color(255, 0, 0), 0.2f);
			}
		}
	}

}
