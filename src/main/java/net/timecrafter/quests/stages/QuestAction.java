package net.timecrafter.quests.stages;

/**
 * Represents a quest stage which has no criteria or conditions and is executed immediately if available
 */
public interface QuestAction extends QuestStage {

	/**
	 * @return the amount of time in ticks that should be waited before executing another action after this
	 */
	int getTickDelay();

}
