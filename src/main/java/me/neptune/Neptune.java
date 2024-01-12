/**
 * A class to initialize and hold the Singleton of Neptune Client.
 */
package me.neptune;

import me.neptune.cmd.CommandManager;
import me.neptune.gui.HudManager;
import me.neptune.module.ModuleManager;
import me.neptune.module.modules.SubEvent;
import me.neptune.settings.Settings;
import me.neptune.utils.RenderUtils;
import meteordevelopment.orbit.EventBus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.DrawContext;

import java.lang.invoke.MethodHandles;

public final class Neptune implements ModInitializer {

	/**
	 * Initializes Neptune Client and creates sub-systems.
	 */
	@Override
	public void onInitialize() {
		System.out.println("[" + Neptune.NAME + "] Starting Client");
		System.out.println("[" + Neptune.NAME + "] Register eventbus");
		EVENT_BUS.registerLambdaFactory("me.neptune", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		RENDER = new RenderUtils();
		System.out.println("[" + Neptune.NAME + "] Reading Settings");
		SETTINGS = new Settings();
		System.out.println("[" + Neptune.NAME + "] Initializing Modules");
		MODULE = new ModuleManager();
		System.out.println("[" + Neptune.NAME + "] Initializing Commands");
		COMMAND = new CommandManager();
		System.out.println("[" + Neptune.NAME + "] Initializing GUI");
		HUD = new HudManager();
		System.out.println("[" + Neptune.NAME + "] Loading SubEvent");
		SUB = new SubEvent();
		EVENT_BUS.subscribe(SUB);
		System.out.println("[" + Neptune.NAME + "] Initialized and ready to play!");

		System.out.println("Welcome to Neptune Client!");
	}

	public static final String NAME = "Neptune";
	public static final String VERSION = "1.0.0";
	public static final String PREFIX = "+";
	public static final EventBus EVENT_BUS = new EventBus();
	// Systems
	public static ModuleManager MODULE;
	public static CommandManager COMMAND;
	public static HudManager HUD;
	public static Settings SETTINGS;
	public static RenderUtils RENDER;
	public static SubEvent SUB;

	/**
	 * Updates Neptune on a per-tick basis.
	 */
	public static void update() {
		MODULE.update();
		HUD.update();
	}

	/**
	 * Renders the HUD every frame
	 * @param context The current Matrix Stack
	 * @param partialTicks Delta between ticks
	 */
	public static void drawHUD(DrawContext context, float partialTicks) {
		if (!HUD.isClickGuiOpen()) HUD.draw(context, partialTicks);
	}

	/**
	 * Called when the client is shutting down.
	 */
	public static void endClient() {
		SETTINGS.saveSettings();
		System.out.println("[" + Neptune.NAME + "] Shutting down...");
	}
}
