package net.timecrafter.quests.stages.generic;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.timecrafter.custombukkit.entities.CustomEntity;
import net.timecrafter.quests.QuestPlugin;
import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.QuestAction;

public class EntityTalkAction implements QuestAction {

	private final CustomEntity entity;
	private final String message;

	/**
	 * An action which prints a single line of text as if spoken by the given entity
	 *
	 * @param entity who is speaking
	 * @param message which the entity will say
	 */
	public EntityTalkAction(CustomEntity entity, String message) {
		this.entity = entity;
		this.message = message;
	}

	@Override
	public void execute(QuestParty party) {
		for (Player player : party.getOnlinePartyMembers()) {
			player.sendMessage(formatAsEntity(message, entity));
		}
	}

	@Override
	public boolean isAvailable(QuestParty party) {
		return entity.isAlive();
	}

	@Override
	public int getTickDelay() {
		return 20 * 3;
	}

	public static String formatAsEntity(String message, CustomEntity entity) {
		return entity.getEntityData().getDisplayName() + " " + ChatColor.DARK_GRAY + QuestPlugin.ARROWS + " "
				+ ChatColor.RESET + message;
	}

}
