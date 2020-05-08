package net.timecrafter.quests.quests.stages;

import org.bukkit.entity.Player;

/**
 * Represents a generic stage of a quest such as a character speaking or a mission to collect something
 */
public interface QuestStage {

	/**
	 * Executes the action
	 *
	 * @param player who the action is being executed for
	 */
	void execute(Player player);

	/**
	 * Whether this stage can/should be executed for the given player
	 *
	 * @param player who is trying to execute this stage
	 * @return whether {@link #execute(Player)} is available
	 */
	boolean isAvailable(Player player);

}
