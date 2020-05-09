package net.timecrafter.quests.activation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collection;
import java.util.Objects;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.timecrafter.custombukkit.entities.CustomEntity;
import net.timecrafter.quests.data.Quest;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.generic.EntityTalkAction;
import net.timecrafter.quests.stages.QuestStage;
import net.timecrafter.quests.stages.QuestTask;

public class EntityInteractMethod implements ActivationMethod {

	private final Quest quest;
	private final int entityId;
	private final String hint;

	/**
	 * An activation method that is triggered when the given entity is clicked
	 *
	 * @param quest which is being activated
	 * @param entityId of the entity that must be right clicked to activate a quest
	 * @param hint on how to trigger this method
	 */
	public EntityInteractMethod(Quest quest, int entityId, String hint) {
		this.quest = Objects.requireNonNull(quest);
		this.entityId = entityId;
		this.hint = Objects.requireNonNull(hint);
	}

	@EventHandler
	public void onCitizensInteract(NPCClickEvent event) {
		NPC npc = event.getNPC();
		Collection<CustomEntity> possibilities =
				CustomEntity.getSpecificEntities(entity ->
						entity.getCurrentLocation().equals(npc.getStoredLocation()) && entity.getEntityData().getDatabaseId() == entityId);
		if (!possibilities.isEmpty()) {
			Player player = event.getClicker();
			QuestParty party = QuestParty.getOrCreateParty(player);

			boolean started = quest.startQuest(party);
			if (!started) {
				CustomEntity entity = possibilities.iterator().next();
				String message = null;
				if (party.getQuest() == quest) {
					QuestStage currentStage = party.getCurrentStage();
					if (currentStage instanceof QuestTask) {
						message = ((QuestTask) currentStage).getTaskDescription();
					}
				} else if (!quest.isPartyEligible(party)) {
					message = ChatColor.GRAY + "You can't start that quest just yet...";
				}

				if (message != null) {
					player.sendMessage(EntityTalkAction.formatAsEntity(message, entity));
				}
			}
		}
	}

	@Override
	public String getHint() {
		return hint;
	}

}
