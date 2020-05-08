package net.timecrafter.quests.events;

import net.timecrafter.quests.data.LinearQuest;
import net.timecrafter.quests.party.QuestParty;

public class QuestCompletionEvent extends QuestEvent {

	/**
	 * An event that occurs when all stages of a quest are completed by a player
	 *
	 * @param quest which was completed
	 * @param party which completed the quest
	 */
	public QuestCompletionEvent(LinearQuest quest, QuestParty party) {
		super(quest, party);
	}

}
