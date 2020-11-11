package net.timecrafter.quests.stages.generic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.SimpleProgressQuestTask;

public class GiveEntityItemTask extends SimpleProgressQuestTask {

	private final int entityId;
	private final Set<UUID> playersCompleted = new HashSet<>();

	private Material[] itemMaterials;
	private ItemStack[] comparisonItems;

	private String requiredItemName;
	private String entityName;

	/**
	 * A task which requires a player to right click an entity with a specific type of item in their inventory.
	 *
	 * @param entityId of the entity which the item should be given to
	 * @param material type of item which the player will need
	 */
	public GiveEntityItemTask(int entityId, Material... material) {
		this.entityId = entityId;
		this.itemMaterials = material;
	}

	/**
	 * A task which requires a player to right click an entity with a specific item in their inventory.
	 *
	 * @param entityId of the entity which the item should be given to
	 * @param comparisonItem which {@link ItemStack#isSimilar(ItemStack)} to the required item
	 */
	public GiveEntityItemTask(int entityId, ItemStack... comparisonItem) {
		this.entityId = entityId;
		this.comparisonItems = comparisonItem;
	}

	/**
	 * Sets the name of the entity which should be shown to the player to describe which entity to right click. Has no
	 * effect on functionality, purely used as a hint to the player.
	 *
	 * @param name of the entity to right click
	 */
	public GiveEntityItemTask setEntityName(String name) {
		this.entityName = name;
		return this;
	}

	/**
	 * Sets the name of the item which describes which item should be given to the entity. Has no
	 * effect on functionality, purely used as a hint to the player.
	 *
	 * @param name of the item to give to the entity
	 */
	public GiveEntityItemTask setRequiredItemName(String name) {
		this.requiredItemName = name;
		return this;
	}

	/**
	 * Compares an item with the details of the required item given to this class
	 *
	 * @param item to compare to this class's details
	 * @return whether the specified item matches the item required by this task
	 */
	private boolean compareItems(ItemStack item) {
		if (comparisonItems != null) {
			for (ItemStack comparisonItem : comparisonItems) {
				if (comparisonItem.isSimilar(item)) {
					return true;
				}
			}
			return false;
		} else if (itemMaterials != null) {
			for (Material material : itemMaterials) {
				if (item.getType() == material) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent event) {
		NPC npc = event.getNPC();
		if (npc.getId() == entityId) {
			Player player = event.getClicker();

			ListIterator<ItemStack> inventoryIterator = player.getInventory().iterator();
			while (inventoryIterator.hasNext()) {
				ItemStack item = inventoryIterator.next();
				if (compareItems(item)) {
					inventoryIterator.remove();
					playersCompleted.add(player.getUniqueId());
				}
			}
		}
	}

	@Override
	public String getTaskDescription() {
		return ChatColor.GRAY + "Hand in your"
				+ (requiredItemName != null ? " " + requiredItemName : "")
				+ (entityName != null ? ChatColor.GRAY + " to " + entityName : "");
	}

	@Override
	public boolean isComplete(QuestParty party) {
		for (UUID uuid : party.getPartyMembers()) {
			if (!playersCompleted.contains(uuid)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getProgress(QuestParty party) {
		int progress = 0;
		for (UUID uuid : party.getPartyMembers()) {
			if (playersCompleted.contains(uuid)) {
				progress++;
			}
		}
		return progress;
	}

}
