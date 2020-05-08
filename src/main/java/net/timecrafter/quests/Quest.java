package net.timecrafter.quests;

import org.bukkit.Bukkit;

import com.google.common.collect.ImmutableList;
import java.util.*;
import net.timecrafter.quests.activation.ActivationMethod;
import net.timecrafter.quests.events.QuestCompletionEvent;
import net.timecrafter.quests.events.QuestStageProgressionEvent;
import net.timecrafter.quests.events.QuestStartEvent;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.QuestAction;
import net.timecrafter.quests.stages.QuestStage;
import net.timecrafter.quests.stages.QuestTask;

public abstract class Quest {

	private final String questName;
	private final String description;
	private final QuestType questType;
	private final int minimumLevel;

	private final List<QuestStage> stages;
	private final Set<QuestParty> parties = new HashSet<>();

	/**
	 * An object which represents a line of linear events, which are instances of {@link QuestStage}, and handles the
	 * sequence for players.
	 *
	 * @param questName the name of this quest shown to players
	 * @param description a description of what this quest is about shown to players
	 * @param questType the type of quest
	 */
	public Quest(String questName, String description, QuestType questType, int minimumLevel,
			List<QuestStage> finalStages, ActivationMethod activationMethod) {
		this.questName = questName;
		this.description = description;
		this.questType = questType;
		this.minimumLevel = minimumLevel;
		this.stages = finalStages; // To make sure no edits to the list can happen after instantiation

		if (stages.isEmpty()) {
			throw new IllegalStateException("A quest must have at least 1 stage");
		}

		// All values need to be unique
		for (QuestStage stage : stages) {
			for (QuestStage comparingStage : stages) {
				if (stage == comparingStage) {
					throw new IllegalStateException("Duplicate stage object in stage list");
				}
			}
		}

		Bukkit.getPluginManager().registerEvents(activationMethod, QuestPlugin.getInstance());
	}

	/**
	 * @return the name of this quest
	 */
	public String getQuestName() {
		return questName;
	}

	/**
	 * @return the summary details of this quest
	 */
	public String getQuestDescription() {
		return description;
	}

	/**
	 * @return the type of this quest
	 */
	public QuestType getQuestType() {
		return questType;
	}

	/**
	 * @return the minimum level required to start this quest
	 */
	public int getMinimumLevel() {
		return minimumLevel;
	}

	/**
	 * Starts a {@link QuestParty} of players on this quest at the first stage
	 *
	 * @param party who is starting the quest
	 */
	public void start(QuestParty party) {
		setStage(party, stages.get(0));
		Bukkit.getPluginManager().callEvent(new QuestStartEvent(this, party));
	}

	/**
	 * Progresses a player to the next {@link QuestStage} in the sequence
	 *
	 * @param party who is moving to the next stage
	 * @return whether the next stage was available
	 */
	public boolean nextStage(QuestParty party) {
		if (parties.contains(party)) {
			int nextIndex = stages.indexOf(party.getCurrentStage()) + 1;
			if (nextIndex < stages.size()) {
				QuestStage nextStage = stages.get(nextIndex);
				setStage(party, nextStage);
				Bukkit.getPluginManager().callEvent(new QuestStageProgressionEvent(this, party, nextStage));
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets a party's current {@link QuestStage} and handles the execution of that stage accordingly. Note: This method
	 * is private because it is dangerous to use out of sequence due to the fact that it calls {@link
	 * #nextStage(QuestParty)} which calls this method again in turn. Editing this method incorrectly may result in a
	 * stack overflow.
	 *
	 * @param party to set the stage for
	 * @param stage to be executed
	 */
	private void setStage(QuestParty party, QuestStage stage) {
		int index = stages.indexOf(stage);

		parties.add(party);
		party.setCurrentStage(stage);
		stage.execute(party); // Begins the stage

		int tickDelay = -1;
		Runnable runnable;
		if (stage instanceof QuestAction) { // And the current stage is not awaiting criteria
			QuestAction action = (QuestAction) stage;
			tickDelay = action.getTickDelay();
		}
		if (stage instanceof QuestTask) {
			QuestTask task = (QuestTask) stage;
			if (task.isComplete(party) && tickDelay < 0) { // If 0 or more, it was an action with a custom time
				tickDelay = 0;
			} else if (!task.isComplete(party)) {
				tickDelay = -1; // Don't progress if the task isn't complete
			}
		}

		if (index + 1 < stages.size()) { // If there is a next stage
			runnable = () -> {
				boolean success = nextStage(party);
				if (!success) {
					QuestPlugin.getInstance().getLogger().warning(
							"Failed to progress quest for a party, which is an error");
				}
			};
		} else {
			runnable = () -> completeQuest(party);
		}

		if (tickDelay > -1) {
			if (tickDelay > 0) {
				Bukkit.getScheduler().runTaskLater(QuestPlugin.getInstance(), runnable, tickDelay);
			} else {
				runnable.run(); // No point scheduling a task with 0 delay
			}
		}
	}

	/**
	 * Calls {@link #onQuestCompletion(QuestParty)} and {@link QuestCompletionEvent} for the player
	 *
	 * @param party who has completed this quest
	 */
	private void completeQuest(QuestParty party) {
		onQuestCompletion(party);
		Bukkit.getPluginManager().callEvent(new QuestCompletionEvent(this, party));
		parties.remove(party);
	}

	/**
	 * @return an immutable list of all the stages in this quest
	 */
	public List<QuestStage> getStages() {
		return ImmutableList.copyOf(stages);
	}

	/**
	 * An internal abstract method which is called when a player completes the quest. This saves you having to make a
	 * listener for {@link QuestCompletionEvent} if you don't need to.
	 *
	 * @param party who has completed the quest
	 */
	public abstract void onQuestCompletion(QuestParty party);

}
