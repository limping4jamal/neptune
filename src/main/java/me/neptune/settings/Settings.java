/**
 * A class to represent a basic Settings file manager.
 */
package me.neptune.settings;

import com.google.common.base.Splitter;
import me.neptune.Neptune;
import me.neptune.gui.HudManager;
import me.neptune.module.Module;
import me.neptune.utils.Wrapper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Settings implements Wrapper {
	private final File neptuneOptions;

	private static final Hashtable<String, String> settings = new Hashtable<>();

	public Settings() {
		neptuneOptions = new File(mc.runDirectory, "Neptune_Config.txt");
		readSettings();
	}

	public void saveSettings() {
		PrintWriter printwriter = null;
		try {
			printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(neptuneOptions), StandardCharsets.UTF_8));

			// Write HUD information and 'other' settings.
			printwriter.println("x:" + HudManager.hud.getX());
			printwriter.println("y:" + HudManager.hud.getY());

			printwriter.println("armor_x:" + Neptune.HUD.armorHud.getX());
			printwriter.println("armor_y:" + Neptune.HUD.armorHud.getY());

			for (Setting setting : HudManager.SETTINGS) {
				if(setting instanceof BooleanSetting bs) {
					printwriter.println(bs.getLine() + ":" + bs.getValue());
				}else if (setting instanceof SliderSetting ss) {
					printwriter.println(ss.getLine() + ":" + ss.getValue());
				} else if (setting instanceof BindSetting bs) {
					printwriter.println(bs.getLine() + ":" + bs.getKey());
				}
			}
			// Write Module Settings
			for (Module module : Neptune.MODULE.modules) {
				for (Setting setting : module.getSettings()) {
					if(setting instanceof BooleanSetting bs) {
						printwriter.println(bs.getLine() + ":" + bs.getValue());
					}else if (setting instanceof SliderSetting ss) {
						printwriter.println(ss.getLine() + ":" + ss.getValue());
					} else if (setting instanceof BindSetting bs) {
						printwriter.println(bs.getLine() + ":" + bs.getKey());
					} else if (setting instanceof EnumSetting es) {
						printwriter.println(es.getLine() + ":" + es.getValue().name());
					}
				}
				printwriter.println(module.getName().toLowerCase() + "_state:" + module.isOn());
			}
		} catch (Exception exception) {
			System.out.println("[" + Neptune.NAME + "] Failed to save settings");
		} finally {
			IOUtils.closeQuietly(printwriter);
		}
	}

	public void readSettings() {
		final Splitter COLON_SPLITTER = Splitter.on(':');
		try {
			if (!this.neptuneOptions.exists()) {
				return;
			}
			List<String> list = IOUtils.readLines(new FileInputStream(this.neptuneOptions), StandardCharsets.UTF_8);
			for (String s : list) {
				try {
					Iterator<String> iterator = COLON_SPLITTER.limit(2).split(s).iterator();
					settings.put(iterator.next(), iterator.next());
				} catch (Exception var10) {
					System.out.println("Skipping bad option: " + s);
				}
			}
			//KeyBinding.updateKeysByCode();
		} catch (Exception exception) {
			System.out.println("[" + Neptune.NAME + "] Failed to load settings");
		}
	}

	public static boolean isInteger(final String str) {
		final Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	public static boolean isFloat(String str) {
		String pattern = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";
		return str.matches(pattern);
	}
	public static int getSettingInt(String setting) {
		String s = settings.get(setting);
		if(s == null || !isInteger(s)) return -1;
		return Integer.parseInt(s);
	}

	public static float getSettingFloat(String setting, float defaultValue) {
		String s = settings.get(setting);
		if (s == null || !isFloat(s)) return defaultValue;
		return Float.parseFloat(s);
	}
	
	public static boolean getSettingBoolean(String setting) {
		String s = settings.get(setting);
		return Boolean.parseBoolean(s);
	}

	public static boolean getSettingBoolean(String setting, boolean value) {
		if (settings.get(setting) != null) {
			String s = settings.get(setting);
			return Boolean.parseBoolean(s);
		} else {
			return value;
		}
	}
	
	public static String getSettingString(String setting) {
		return settings.get(setting);
	}
}
