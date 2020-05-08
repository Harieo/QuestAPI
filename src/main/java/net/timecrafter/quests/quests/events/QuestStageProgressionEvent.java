package net.timecrafter.quests.quests.events;

import org.bukkit.entity.Player;

import net.timecrafter.quests.quests.Quest;
import net.timecrafter.quests.quests.stages.QuestStage;

public class QuestStageProgressionEvent extends QuestEvent {

	private final QuestStage stage;

	/**
	 * An event that occurs when a player progresses to the next stage of a quest line
	 *
	 * @param quest which the player is currently doing
	 * @param player who is progressing to the next stage
	 * @param stage that is being progressed to
	 */
	public QuestStageProgressionEvent(Quest quest, Player player, QuestStage stage) {
		super(quest, player);
		this.stage = stage;
	}

	/**
	 * @return the stage that is being progressed to in the quest
	 */
	public QuestStage getNextStage() {
		return stage;
	}

}
