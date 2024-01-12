/**
 * A class to represent a system that manages all of the Modules.
 */
package me.neptune.module;

import me.neptune.gui.ClickUI;
import me.neptune.module.modules.client.Cape;
import me.neptune.module.modules.client.ChatSuffix;
import me.neptune.module.modules.client.ClickGui;
//import me.neptune.module.modules.client.RPC;
import me.neptune.module.modules.client.Test;
import me.neptune.module.modules.combat.*;
import me.neptune.module.modules.misc.*;
import me.neptune.module.modules.movement.*;
import me.neptune.module.modules.render.*;
import me.neptune.module.modules.world.AutoSign;
import me.neptune.module.modules.world.Freecam;
import me.neptune.module.modules.world.TileBreaker;
import me.neptune.settings.Settings;
import me.neptune.utils.RenderUtils;
import me.neptune.utils.Wrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModuleManager implements Wrapper {
	public ArrayList<Module> modules = new ArrayList<>();
	public static Module lastLoadModule;
	public ModuleManager() {
		// Look at all these modules!
		addModule(new ClickGui());
		addModule(new AnchorAura());
		addModule(new AntiCactus());
		addModule(new AntiInvis());
		addModule(new Velocity());
		addModule(new AutoEat());
		addModule(new AutoFish());
		addModule(new AutoSign());
		addModule(new AutoRespawn());
		addModule(new AutoTotem());
		addModule(new AutoWalk());
		addModule(new Burrow());
		addModule(new ChestESP());
		addModule(new EntityESP());
		addModule(new FastPlace());
		addModule(new Fullbright());
		addModule(new ItemESP());
		addModule(new InvMove());
		addModule(new NoFall());
		addModule(new NoRender());
		addModule(new NoSlowdown());
		addModule(new PlayerESP());
		addModule(new Reach());
		addModule(new Speed());
		addModule(new SpawnerESP());
		addModule(new Sprint());
		addModule(new Step());
		addModule(new Faster());
		addModule(new TileBreaker());
		addModule(new Test());
		addModule(new KillEffect());
		addModule(new FakePlayer());
		addModule(new Freecam());
		addModule(new NoRotate());
		addModule(new BurrowStrafe());
		addModule(new MultiTask());
		addModule(new XCarry());
		addModule(new Blocker());
		addModule(new ChatSuffix());
		addModule(new PortalChat());
		addModule(new HolePush());
		addModule(new Surround());
		addModule(new PacketMine());
		addModule(new HandModifier());
		addModule(new AutoCrystal());
		addModule(new NoEntityTrace());
		addModule(new TntAura());
		//addModule(new RPC());
		addModule(new AirPlace());
		addModule(new Cape());
		addModule(new FastFall());
		addModule(new RaytraceBypass());
	}

	public void onKeyReleased(int eventKey) {
		if (eventKey == -1 || eventKey == 0 || mc.currentScreen instanceof ClickUI) {
			return;
		}
		modules.forEach(module -> {
			if (module.getBind().getKey() == eventKey) {
				module.disable();
			}
		});
	}

	public boolean setBind(int eventKey) {
		if (eventKey == -1 || eventKey == 0) {
			return false;
		}
		AtomicBoolean set = new AtomicBoolean(false);
		modules.forEach(module -> {
			if (module.getBind().isListening()) {
				module.getBind().setKey(eventKey);
				module.getBind().setListening(false);
				if (module.getBind().getBind().equals("DELETE")) {
					module.getBind().setKey(-1);
				}
				set.set(true);
			}
		});
		return set.get();
	}
	public void onKeyPressed(int eventKey) {
		if (eventKey == -1 || eventKey == 0 || mc.currentScreen instanceof ClickUI) {
			return;
		}
		modules.forEach(module -> {
			if (module.getBind().getKey() == eventKey) {
				module.toggle();
			}
		});
	}
	
	public void update() {
		for(Module module : modules) {
			if(module.isOn()) {
				module.onUpdate();
			}
		}
	}
	
	public void render(MatrixStack matrixStack) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		
		
		matrixStack.push();
		RenderUtils.applyRenderOffset(matrixStack);
		for(Module module : modules) {
			if(module.isOn()) {
				module.onRender(matrixStack, MinecraftClient.getInstance().getTickDelta());
			}
		}
		matrixStack.pop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	public void sendPacket(Packet<?> packet) {
		for(Module module : modules) {
			if(module.isOn()) {
				module.onSendPacket(packet);
			}
		}
	}
	
	public void addModule(Module module) {
		modules.add(module);
		module.setState(Settings.getSettingBoolean(module.getName().toLowerCase() + "_state"));
		int key = Settings.getSettingInt(module.getName().toLowerCase() + "_bind");
		if (key == -1) return;
		module.getBind().setKey(key);
	}
	/*
	public void disableAll() {
		for(Module module : modules) {
			module.disable();
		}
	}

	 */
	
	public Module getModuleByName(String string) {
		for(Module module : modules) {
			if(module.getName().equalsIgnoreCase(string)) {
				return module;
			}
		}
		return null;
	}
}
