package me.neptune.interfaces;

public interface IMinecraftClient {
	public void rightClick();
	
	public void setItemUseCooldown(int itemUseCooldown);

	public int getItemUseCooldown();
	
	public IWorld getWorld();
}
