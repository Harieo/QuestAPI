package net.timecrafter.quests.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.timecrafter.quests.Quest;
import net.timecrafter.quests.party.QuestParty;

public class QuestEvent extends Event {

	private final Quest quest;
	private final QuestParty party;

	public QuestEvent(Quest quest, QuestParty party) {
		this.quest = quest;
		this.party = party;
	}

	public Quest getQuest() {
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
