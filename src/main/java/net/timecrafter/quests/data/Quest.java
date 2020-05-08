package net.timecrafter.quests.data;

import java.util.List;
import net.timecrafter.quests.events.QuestCompletionEvent;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.QuestStage;

public interface Quest {

	/**
	 * @return the name of this quest
	 */
	String getQuestName();

	/**
	 * @return the summary details of this quest
	 */
	String getQuestDescription();

	/**
	 * @return the type of this quest
	 */
	QuestType getQuestType();

	/**
	 * @return the minimum level required to start this quest
	 */
	int getMinimumLevel();

	/**
	 * Starts a {@link QuestParty} of players on this quest at the first stage
	 *
	 * @param party who is starting the quest
	 */
	void startQuest(QuestParty party);

	/**
	 * Progresses a party to the next {@link QuestStage} in the sequence
	 *
	 * @param party who is moving to the next stage
	 * @return whether the next stage was available
	 */
	boolean nextStage(QuestParty party);

	/**
	 * Sets a party's current {@link QuestStage} and handles the execution of that stage accordingly. Note: This method
	 * is private because it is dangerous to use out of sequence due to the fact that it calls {@link
	 * #nextStage(QuestParty)} which calls this method again in turn. Editing this method incorrectly may result in a
	 * stack overflow.
	 *
	 * @param party to set the stage for
	 * @param stage to be executed
	 */
	void setStage(QuestParty party, QuestStage stage);

	/**
	 * Calls {@link #onQuestCompletion(QuestParty)} and {@link QuestCompletionEvent} for the player
	 *
	 * @param party who has completed this quest
	 */
	void completeQuest(QuestParty party);

	/**
	 * @return a list of all the stages in this quest
	 */
	List<QuestStage> getStages();

	/**
	 * An internal abstract method which is called when a player completes the quest. This saves you having to make a
	 * listener for {@link QuestCompletionEvent} if you don't need to.
	 *
	 * @param party who has completed the quest
	 */
	void onQuestCompletion(QuestParty party);

}
