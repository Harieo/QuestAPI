package net.timecrafter.quests.stages.generic;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import net.timecrafter.custombukkit.entities.CustomEntity;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.QuestAction;

public class EntityTalkAction implements QuestAction {

	private final Set<CustomEntity> validEntities = new HashSet<>();
	private final String message;

	/**
	 * An action which prints a single line of text as if spoken by the given entity
	 *
	 * @param entityId of the entity which is talking
	 * @param message which the entity will say
	 */
	public EntityTalkAction(int entityId, String message) {
		validEntities.addAll(CustomEntity.getSpecificEntities(entity -> entity.getEntityId() == entityId));
		if (validEntities.isEmpty()) {
			System.out.println("EntityTalkAction has no entities to use with id " + entityId);
		}
		this.message = message;
	}

	@Override
	public void execute(QuestParty party) {
		if (isAvailable(party)) {
			for (Player player : party.getOnlinePartyMembers()) {
				player.sendMessage(formatAsEntity(message, validEntities.iterator().next()));
			}
		}
	}

	@Override
	public boolean isAvailable(QuestParty party) {
		return !validEntities.isEmpty();
	}

	@Override
	public int getTickDelay() {
		return 20 * 3;
	}

	public static String formatAsEntity(String message, CustomEntity entity) {
		return entity.getEntityData().getDisplayName() + " " + ChatColor.DARK_GRAY + " » "
				+ ChatColor.RESET + message;
	}

}
