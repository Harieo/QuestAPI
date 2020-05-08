package net.timecrafter.quests.activation;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.Objects;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.timecrafter.custombukkit.entities.CustomEntity;
import net.timecrafter.quests.data.Quest;
import net.timecrafter.quests.party.QuestParty;

public class EntityInteractMethod implements ActivationMethod {

	private final Quest quest;
	private final CustomEntity entity;

	/**
	 * An activation method that is triggered when the given entity is clicked
	 *
	 * @param quest which is being activated
	 * @param entity that must be right clicked to activate a quest
	 */
	public EntityInteractMethod(Quest quest, CustomEntity entity) {
		this.quest = Objects.requireNonNull(quest);
		this.entity = Objects.requireNonNull(entity);
	}

	@EventHandler
	public void onCitizensInteract(NPCClickEvent event) {
		NPC npc = event.getNPC();
		if (npc != null && npc.equals(entity.getNPC())) {
			quest.startQuest(QuestParty.getOrCreateParty(event.getClicker()));
		}
	}

	@Override
	public String getHint() {
		return ChatColor.GRAY + "Talk to " + ChatColor.YELLOW + entity.getEntityData().getDisplayName() + ChatColor.GRAY
				+ " to start the quest";
	}

}
