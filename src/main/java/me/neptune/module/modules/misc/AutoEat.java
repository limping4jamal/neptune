/**
 * autoEat Module
 */
package me.neptune.module.modules.misc;

import me.neptune.module.Module;
import me.neptune.settings.SliderSetting;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

public class AutoEat extends Module {
	public static AutoEat INSTANCE;
	private final SliderSetting hunger;
	public AutoEat() {
		super("AutoEat", Category.Misc);
		this.setDescription("Automatically eats the best food in your inventory.");
		INSTANCE = this;

		hunger = new SliderSetting("Hunger", "autoeat_hunger", 0f, 0f, 19f, 1f);
		this.addSetting(hunger);
	}

	@Override
	public void onUpdate() {
		if(mc.player.getHungerManager().getFoodLevel() <= hunger.getValueInt()) {
			int foodSlot= -1;
			FoodComponent bestFood = null;
			for(int i = 0; i< 9; i++) {
				Item item = mc.player.getInventory().getStack(i).getItem();
				
				if(!item.isFood()) {
					continue;
				}
				FoodComponent food = item.getFoodComponent();
				if(bestFood != null) {
					if(food.getHunger() > bestFood.getHunger()) {
						bestFood = food;
						foodSlot = i;
					}
				}else {
					bestFood = food;
					foodSlot = i;
				}
				
			}
			
		    if(bestFood != null) {
		    	mc.player.getInventory().selectedSlot = foodSlot;
		    	mc.options.useKey.setPressed(true);
		    }
		}
    }
}
