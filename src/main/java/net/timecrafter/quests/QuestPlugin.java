package net.timecrafter.quests;

import org.bukkit.plugin.java.JavaPlugin;

public class QuestPlugin extends JavaPlugin {

	public static final char ARROWS = '»';

	private static QuestPlugin instance;

	@Override
	public void onEnable() {
		instance = this;
	}

	public static QuestPlugin getInstance() {
		return instance;
	}

}
