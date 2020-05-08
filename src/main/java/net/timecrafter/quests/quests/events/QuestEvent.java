package net.timecrafter.quests.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.timecrafter.quests.quests.Quest;

public class QuestEvent extends Event {

	private final Quest quest;
	private final Player player;

	public QuestEvent(Quest quest, Player player) {
		this.quest = quest;
		this.player = player;
	}

	public Quest getQuest() {
		return quest;
	}

	public Player getPlayer() {
		return player;
	}

	private static final HandlerList handlerList = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
