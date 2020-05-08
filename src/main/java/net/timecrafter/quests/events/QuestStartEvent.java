package net.timecrafter.quests.events;

import net.timecrafter.quests.data.LinearQuest;
import net.timecrafter.quests.party.QuestParty;

public class QuestStartEvent extends QuestEvent {

	/**
	 * An event that occurs when a player starts a quest from the beginning
	 *
	 * @param quest which the player has started
	 * @param party which is starting the quest
	 */
	public QuestStartEvent(LinearQuest quest, QuestParty party) {
		super(quest, party);
	}

}
