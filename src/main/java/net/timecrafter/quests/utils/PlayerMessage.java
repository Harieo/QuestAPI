package net.timecrafter.quests.utils;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A bungee chat API message which takes a player as a parameter for customisable messages
 */
public interface PlayerMessage {

	/**
	 * Returns a message to be sent to the specified player
	 *
	 * @param player who will receive the message
	 * @return the message
	 */
	BaseComponent[] getMessage(Player player);

}
