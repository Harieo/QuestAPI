package net.timecrafter.quests.events;

import net.timecrafter.quests.data.Quest;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.QuestStage;

public class QuestStageProgressionEvent extends QuestEvent {

	private final QuestStage stage;

	/**
	 * An event that occurs when a player progresses to the next stage of a quest line
	 *
	 * @param quest which the player is currently doing
	 * @param party who is progressing to the next stage
	 * @param stage that is being progressed to
	 */
	public QuestStageProgressionEvent(Quest quest, QuestParty party, QuestStage stage) {
		super(quest, party);
		this.stage = stage;
	}

	/**
	 * @return the stage that is being progressed to in the quest
	 */
	public QuestStage getNextStage() {
		return stage;
	}

}
