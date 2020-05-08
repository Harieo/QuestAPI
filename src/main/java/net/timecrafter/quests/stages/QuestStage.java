package net.timecrafter.quests.stages;

import org.bukkit.entity.Player;

import net.timecrafter.quests.party.QuestParty;

/**
 * Represents a generic stage of a quest such as a character speaking or a mission to collect something
 */
public interface QuestStage {

	/**
	 * Executes the action for a given party. Note: All members of the party will be at the same stage so individual
	 * player tracking may not be necessary.
	 *
	 * @param party of people who may be doing part of this quest
	 */
	void execute(QuestParty party);

	/**
	 * Whether this stage can/should be executed for the given party
	 *
	 * @param party who is trying to execute this stage
	 * @return whether {@link #execute(QuestParty)} is available
	 */
	boolean isAvailable(QuestParty party);

}
