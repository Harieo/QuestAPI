package net.timecrafter.quests.data;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import com.google.common.collect.ImmutableList;
import java.util.*;
import java.util.concurrent.ExecutionException;
import net.timecrafter.custombukkit.levelling.PlayerLevellingInfo;
import net.timecrafter.custombukkit.levelling.types.Levelled;
import net.timecrafter.quests.QuestPlugin;
import net.timecrafter.quests.activation.ActivationMethod;
import net.timecrafter.quests.events.QuestCompletionEvent;
import net.timecrafter.quests.events.QuestStageProgressionEvent;
import net.timecrafter.quests.events.QuestStartEvent;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.sql.CompletedQuestsInfo;
import net.timecrafter.quests.stages.QuestAction;
import net.timecrafter.quests.stages.QuestStage;
import net.timecrafter.quests.stages.QuestTask;
import net.timecrafter.timelib.database.api.InfoCore;

public abstract class LinearQuest implements Quest {

	private final String questName;
	private final String description;
	private final String uniqueId;
	private final QuestType questType;
	private final int minimumLevel;
	private boolean repeatable;
	private final Levelled levellingUnit;

	private final List<QuestStage> stages;
	private final Set<QuestParty> parties = new HashSet<>();

	/**
	 * An object which represents a line of linear events, which are instances of {@link QuestStage}, and handles the
	 * sequence for players.
	 *
	 * @param questName the name of this quest shown to players
	 * @param description a description of what this quest is about shown to players
	 * @param questType the type of quest
	 * @param minimumLevel the minimum level all party members must be to start the quest
	 * @param finalStages the stages of this quest that are completed in order by each party
	 * @param levellingUnit the levelling class which is queried to verify minimum level
	 */
	public LinearQuest(String questName,
			String description,
			String uniqueId,
			QuestType questType,
			int minimumLevel,
			boolean repeatable,
			List<QuestStage> finalStages,
			Levelled levellingUnit) {
		this.questName = questName;
		this.description = description;
		this.uniqueId = uniqueId;
		this.questType = questType;
		this.minimumLevel = minimumLevel;
		this.repeatable = repeatable;
		this.stages = finalStages; // To make sure no edits to the list can happen after instantiation
		this.levellingUnit = levellingUnit;

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

	@Override
	public String getQuestName() {
		return questName;
	}

	@Override
	public String getQuestDescription() {
		return description;
	}

	@Override
	public String getQuestIdentifier() {
		return uniqueId;
	}

	@Override
	public QuestType getQuestType() {
		return questType;
	}

	@Override
	public int getMinimumLevel() {
		return minimumLevel;
	}

	@Override
	public boolean isQuestRepeatable() {
		return repeatable;
	}

	@Override
	public boolean isPartyEligible(QuestParty party) {
		// Checks the minimum level of all members
		for (UUID uuid : party.getPartyMembers()) {
			try {
				PlayerLevellingInfo levellingInfo = InfoCore.get(PlayerLevellingInfo.class, uuid).get();
				if (levellingInfo.getLevel(levellingUnit) < minimumLevel) {
					return false; // Not a high enough level
				}

				if (!isQuestRepeatable()) {
					CompletedQuestsInfo completedQuestsInfo = InfoCore.get(CompletedQuestsInfo.class, uuid).get();
					if (completedQuestsInfo.isQuestComplete(this)) {
						return false; // Already completed a non-repeatable quest
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false; // An error occurred, better be safe by not continuing
			}
		}

		return true;
	}

	@Override
	public boolean startQuest(QuestParty party) {
		if (!parties.contains(party) && isPartyEligible(party)) {
			setStage(party, stages.get(0));
			Bukkit.getPluginManager().callEvent(new QuestStartEvent(this, party));
			return true;
		}
		return false;
	}

	@Override
	public boolean nextStage(QuestParty party) {
		if (parties.contains(party)) {
			QuestStage currentStage = party.getCurrentStage();
			if (currentStage instanceof QuestTask) {
				QuestTask task = (QuestTask) currentStage;
				if (!task.isComplete(party)) {
					return false; // Not completed their task yet
				}
			}

			int nextIndex = stages.indexOf(currentStage) + 1;
			if (nextIndex < stages.size()) {
				QuestStage nextStage = stages.get(nextIndex);
				setStage(party, nextStage);
				Bukkit.getPluginManager().callEvent(new QuestStageProgressionEvent(this, party, nextStage));
				return true;
			}
		}

		return false;
	}

	@Override
	public void setStage(QuestParty party, QuestStage stage) {
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

	@Override
	public void completeQuest(QuestParty party) {
		onQuestCompletion(party);
		Bukkit.getPluginManager().callEvent(new QuestCompletionEvent(this, party));
		parties.remove(party);
	}

	@Override
	public void cancelQuest(QuestParty party) {
		if (parties.contains(party)) {
			QuestStage currentStage = party.getCurrentStage();
			if (currentStage instanceof QuestTask) {
				((QuestTask) currentStage).cancel(party);
			}

			parties.remove(party);
		}
	}

	@Override
	public List<QuestStage> getStages() {
		return ImmutableList.copyOf(stages);
	}

	/**
	 * @return the method to activate this quest
	 */
	public abstract ActivationMethod getActivationMethod();

}
