package me.neptune.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.neptune.Neptune;
import me.neptune.interfaces.IMinecraftClient;
import me.neptune.module.modules.misc.MultiTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.thread.ReentrantThreadExecutor;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static me.neptune.utils.Wrapper.mc;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements IMinecraftClient{

	@Shadow
	private int itemUseCooldown;

	@Shadow
	public ClientWorld world;

	@Shadow public abstract void close();
	
	public MinecraftClientMixin(String string) {
		super(string);
	}

	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tick(CallbackInfo info) {
		if (this.world != null) {
			Neptune.update();
		}
	}

	@Inject(at = {@At(value = "HEAD") }, method = {"doAttack()Z"}, cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
		if (Neptune.HUD.isClickGuiOpen()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
	
	@Inject(at = {@At(value = "HEAD")}, method = {"close()V"})
	private void onClose(CallbackInfo ci) {
		try {
			Neptune.endClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getItemUseCooldown()
	{
		return itemUseCooldown;
	}
	
	@Override
	public void setItemUseCooldown(int delay)
	{
		this.itemUseCooldown = delay;
	}

	// multi task

	@Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
	public boolean isUsing(ClientPlayerEntity clientPlayerEntity) {
		MultiTask instance = MultiTask.INSTANCE;
		if (instance.isOn()) return false;
		return clientPlayerEntity.isUsingItem();
	}

	@Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
	public boolean isBreaking(ClientPlayerInteractionManager clientPlayerInteractionManager) {
		MultiTask instance = MultiTask.INSTANCE;
		if (instance.isOn()) return false;
		return clientPlayerInteractionManager.isBreakingBlock();
	}

	// multi task
}
