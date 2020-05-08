package net.timecrafter.quests.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.timecrafter.quests.data.LinearQuest;
import net.timecrafter.quests.party.QuestParty;

public class QuestEvent extends Event {

	private final LinearQuest quest;
	private final QuestParty party;

	public QuestEvent(LinearQuest quest, QuestParty party) {
		this.quest = quest;
		this.party = party;
	}

	public LinearQuest getQuest() {
		return quest;
	}

	public QuestParty getQuestParty() {
		return party;
	}

	private static final HandlerList handlerList = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
