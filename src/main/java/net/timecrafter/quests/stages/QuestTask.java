package net.timecrafter.quests.stages;

import net.timecrafter.quests.party.QuestParty;

/**
 * Represents a criteria based quest stage that is held until all criteria are met
 */
public interface QuestTask extends QuestStage {

	/**
	 * A method called if a party has previously invoked {@link #execute(QuestParty)} but has abandoned the quest. If
	 * this is called, the party will have to go through {@link #execute(QuestParty)} again to restart it.
	 *
	 * Developer note: {@link QuestAction} cannot be cancelled because it executes once then moves onto the next stage
	 * immediately.
	 *
	 * @param party who has cancelled this stage
	 */
	void cancel(QuestParty party);

	/**
	 * Indicates that this task has been successfully completed by a party
	 *
	 * @param party which has completed this task
	 */
	void complete(QuestParty party);

	/**
	 * A method to check whether the criteria for completing this task has been met by an executing player
	 *
	 * @param party who is attempting to complete the task
	 * @return whether the task is complete
	 */
	boolean isComplete(QuestParty party);

	/**
	 * Returns a numerical value on how much progress the player has made on the task
	 *
	 * @param party who is attempting to complete the task
	 * @return the progress the player has made towards completing the task over the denominator
	 */
	int getProgress(QuestParty party);

	/**
	 * Returns the maximum amount of progress that can be made on the quest, of which the progress is out of.
	 * For example, a quest with 3 steps will have a denominator of 3 as that is the maximum amount of progress the
	 * player can make.
	 *
	 * @param party who is attempting to complete the task
	 * @return the maximum amount of progress the player can make numerically
	 */
	int getProgressDenominator(QuestParty party);

	/**
	 * @return a message to describe how to complete this task for the player
	 */
	String getTaskDescription();

}
