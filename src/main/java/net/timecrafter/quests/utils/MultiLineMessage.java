package net.timecrafter.quests.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.timecrafter.quests.party.QuestParty;

public class MultiLineMessage {

	private final List<BaseComponent[]> lines = new ArrayList<>();
	private boolean bordered = false;

	/**
	 * A small handler which stores multiple lines to be sent all at once to a player or party of players
	 *
	 * @param lines to initially be stored
	 */
	public MultiLineMessage(String... lines) {
		for (String line : lines) {
			this.lines.add(TextComponent.fromLegacyText(line));
		}
	}

	/**
	 * A small handler which stores multiple lines to be sent all at once to a player or party of players
	 *
	 * @param lines to initially be stored
	 */
	public MultiLineMessage(BaseComponent[]... lines) {
		this.lines.addAll(Arrays.asList(lines));
	}

	/**
	 * Adds a basic string line to the end of the list
	 *
	 * @param line to be added
	 */
	public void addLine(String line) {
		lines.add(TextComponent.fromLegacyText(line));
	}

	/**
	 * Adds a {@link BaseComponent} line to the end of the list
	 *
	 * @param line to be added
	 */
	public void addLine(BaseComponent[] line) {
		lines.add(line);
	}

	/**
	 * Sets whether a blank line border will be sent before and after all the lines of the message
	 *
	 * @param bordered whether to send the border
	 */
	public void setBordered(boolean bordered) {
		this.bordered = bordered;
	}

	/**
	 * Sends all stored lines to the player
	 *
	 * @param player to send the message to
	 */
	public void sendMessage(Player player) {
		if (bordered) {
			player.sendMessage("");
		}
		lines.forEach(player::sendMessage);
		if (bordered) {
			player.sendMessage("");
		}
	}

	/**
	 * Sends the message to all online players of a party
	 *
	 * @param party to send the message to
	 */
	public void sendMessage(QuestParty party) {
		party.getOnlinePartyMembers().forEach(this::sendMessage);
	}

}
