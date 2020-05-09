package net.timecrafter.quests.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.timecrafter.quests.data.Quest;

public class QuestCompleteMessage extends MultiLineMessage {

	/**
	 * An implementation of {@link MultiLineMessage} which sets constant starting values to be consistent over all
	 * quests. Note: You will have to add lines for each reward given for the quest or any custom implementation you'd
	 * like.
	 *
	 * @param quest which has been completed
	 */
	public QuestCompleteMessage(Quest quest) {
		super(new ComponentBuilder("You have completed ").color(ChatColor.GRAY)
				.append(quest.getQuestName()).color(ChatColor.GREEN)
				.event(new HoverEvent(
						Action.SHOW_TEXT,
						TextComponent.fromLegacyText(ChatColor.DARK_AQUA + quest.getQuestDescription()))).create());
		setBordered(true); // Sets as default, can still be disabled
	}

}
