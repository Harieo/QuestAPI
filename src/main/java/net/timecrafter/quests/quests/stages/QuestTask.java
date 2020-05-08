package net.timecrafter.quests.quests.stages;

import org.bukkit.entity.Player;

/**
 * Represents a criteria based quest stage that is held until all criteria are met
 */
public interface QuestTask extends QuestStage {

	/**
	 * A method to check whether the criteria for completing this task has been met by an executing player
	 *
	 * @param player who is attempting to complete the task
	 * @return whether the task is complete
	 */
	boolean isComplete(Player player);

	/**
	 * Returns a numerical value on how much progress the player has made on the task
	 *
	 * @param player who is completing the task
	 * @return the progress the player has made towards completing the task over the denominator
	 */
	int getProgress(Player player);

	/**
	 * Returns the maximum amount of progress that can be made on the quest, of which the progress is out of.
	 * For example, a quest with 3 steps will have a denominator of 3 as that is the maximum amount of progress the
	 * player can make.
	 *
	 * @param player who is completing the quest
	 * @return the maximum amount of progress the player can make numerically
	 */
	int getProgressDenominator(Player player);

}
