package net.timecrafter.quests.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import java.util.*;
import net.timecrafter.quests.QuestPlugin;
import net.timecrafter.quests.quests.activation.ActivationMethod;
import net.timecrafter.quests.quests.events.QuestCompletionEvent;
import net.timecrafter.quests.quests.events.QuestStageProgressionEvent;
import net.timecrafter.quests.quests.events.QuestStartEvent;
import net.timecrafter.quests.quests.stages.QuestAction;
import net.timecrafter.quests.quests.stages.QuestStage;
import net.timecrafter.quests.quests.stages.QuestTask;

public abstract class Quest {

	private final String questName;
	private final String description;
	private final QuestType questType;

	private final List<QuestStage> stages;
	private final Map<UUID, Integer> currentStageMap = new HashMap<>();

	/**
	 * An object which represents a line of linear events, which are instances of {@link QuestStage}, and handles the
	 * sequence for players.
	 *
	 * @param questName the name of this quest shown to players
	 * @param description a description of what this quest is about shown to players
	 * @param questType the type of quest
	 */
	public Quest(String questName, String description, QuestType questType) {
		this.questName = questName;
		this.description = description;
		this.questType = questType;
		this.stages = getFinalStages(); // To make sure no edits to the list can happen after instantiation

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

		Bukkit.getPluginManager().registerEvents(getActivationMethod(), QuestPlugin.getInstance());
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
	 * Starts a player on this quest at the first stage
	 *
	 * @param player who is starting the quest
	 */
	public void start(Player player) {
		setStage(player, stages.get(0));
		Bukkit.getPluginManager().callEvent(new QuestStartEvent(this, player));
	}

	/**
	 * Progresses a player to the next {@link QuestStage} in the sequence
	 *
	 * @param player who is moving to the next stage
	 * @return whether the next stage was available
	 */
	public boolean nextStage(Player player) {
		UUID uuid = player.getUniqueId();
		if (currentStageMap.containsKey(uuid)) {
			int nextIndex = currentStageMap.get(uuid) + 1;
			if (nextIndex < stages.size()) {
				QuestStage nextStage = stages.get(nextIndex);
				setStage(player, nextStage);
				Bukkit.getPluginManager().callEvent(new QuestStageProgressionEvent(this, player, nextStage));
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets a player's current {@link QuestStage} and handles the execution of that stage accordingly. Note: This method
	 * is private because it is dangerous to use out of sequence due to the fact that it calls {@link
	 * #nextStage(Player)} which calls this method again in turn. Editing this method incorrectly may result in a stack
	 * overflow.
	 *
	 * @param player to set the stage for
	 * @param stage to be executed
	 */
	private void setStage(Player player, QuestStage stage) {
		UUID uuid = player.getUniqueId();

		int index = stages.indexOf(stage);
		if (currentStageMap.containsKey(uuid)) {
			currentStageMap.replace(uuid, index);
		} else {
			currentStageMap.put(uuid, index);
		}

		stage.execute(player); // Begins the stage

		int tickDelay = -1;
		Runnable runnable;
		if (stage instanceof QuestAction) { // And the current stage is not awaiting criteria
			QuestAction action = (QuestAction) stage;
			tickDelay = action.getTickDelay();
		}
		if (stage instanceof QuestTask) {
			QuestTask task = (QuestTask) stage;
			if (task.isComplete(player) && tickDelay < 0) { // If 0 or more, it was an action with a custom time
				tickDelay = 0;
			} else if (!task.isComplete(player)) {
				tickDelay = -1; // Don't progress if the task isn't complete
			}
		}

		if (index + 1 < stages.size()) { // If there is a next stage
			runnable = () -> {
				boolean success = nextStage(player);
				if (!success) {
					QuestPlugin.getInstance().getLogger().warning(
							"Failed to progress quest for " + player.getName() + ", which is an error");
				}
			};
		} else {
			runnable = () -> completeQuest(player);
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
	 * Calls {@link #onQuestCompletion(Player)} and {@link QuestCompletionEvent} for the player
	 *
	 * @param player who has completed this quest
	 */
	private void completeQuest(Player player) {
		onQuestCompletion(player);
		Bukkit.getPluginManager().callEvent(new QuestCompletionEvent(this, player));
	}

	/**
	 * @return an immutable list of all the stages in this quest
	 */
	public List<QuestStage> getStages() {
		return ImmutableList.copyOf(stages);
	}

	/**
	 * @return the minimum level a player must be to start this quest
	 */
	public abstract int getMinimumLevel();

	/**
	 * @return the {@link ActivationMethod} which activates this quest for a player
	 */
	public abstract ActivationMethod getActivationMethod();

	/**
	 * An internal abstract method which is called when a player completes the quest. This saves you having to make a
	 * listener for {@link QuestCompletionEvent} if you don't need to.
	 *
	 * @param player who has completed the quest
	 */
	public abstract void onQuestCompletion(Player player);

	/**
	 * @return a list of all the stages in this quest which will be copied to prevent any further editing
	 */
	protected abstract List<QuestStage> getFinalStages();

}
