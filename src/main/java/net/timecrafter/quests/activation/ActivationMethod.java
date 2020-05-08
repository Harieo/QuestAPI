package net.timecrafter.quests.activation;

import org.bukkit.event.Listener;

public interface ActivationMethod extends Listener {

	/**
	 * @return a hint that can be shown to the player on how to trigger the activation method
	 */
	String getHint();

}
