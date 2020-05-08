package net.timecrafter.quests.quests.events;

import org.bukkit.entity.Player;

import net.timecrafter.quests.quests.Quest;

public class QuestCompletionEvent extends QuestEvent {

	/**
	 * An event that occurs when all stages of a quest are completed by a player
	 *
	 * @param quest which was completed
	 * @param player which completed the quest
	 */
	public QuestCompletionEvent(Quest quest, Player player) {
		super(quest, player);
	}

}
