package net.timecrafter.quests;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestPlugin extends JavaPlugin {

	public static final char ARROWS = '»';
	public static final String PREFIX =
			ChatColor.AQUA + ChatColor.BOLD.toString() + "Quest Master " + ChatColor.DARK_GRAY + ARROWS
					+ ChatColor.RESET + " ";

	private static QuestPlugin instance;

	@Override
	public void onEnable() {
		instance = this;
	}

	public static QuestPlugin getInstance() {
		return instance;
	}

	public static String formatMessage(String message) {
		return PREFIX + message;
	}

}
