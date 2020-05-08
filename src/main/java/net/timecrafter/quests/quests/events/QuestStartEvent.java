package net.timecrafter.quests.quests.events;

import org.bukkit.entity.Player;

import net.timecrafter.quests.quests.Quest;

public class QuestStartEvent extends QuestEvent {

	/**
	 * An event that occurs when a player starts a quest from the beginning
	 *
	 * @param quest which the player has started
	 * @param player which is starting the quest
	 */
	public QuestStartEvent(Quest quest, Player player) {
		super(quest, player);
	}

}
