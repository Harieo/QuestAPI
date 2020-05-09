package net.timecrafter.quests.stages.generic;

import net.timecrafter.quests.party.QuestParty;
import net.timecrafter.quests.stages.QuestAction;
import net.timecrafter.quests.utils.PlayerMessage;

public class SendMessageAction implements QuestAction {

	private final PlayerMessage message;
	private final int tickDelay;

	public SendMessageAction(PlayerMessage message) {
		this(message, 0);
	}

	public SendMessageAction(PlayerMessage message, int tickDelay) {
		this.message = message;
		this.tickDelay = tickDelay;
	}

	@Override
	public int getTickDelay() {
		return tickDelay;
	}

	@Override
	public void execute(QuestParty party) {
		party.getOnlinePartyMembers().forEach(player -> player.sendMessage(message.getMessage(player)));
	}

	@Override
	public boolean isAvailable(QuestParty party) {
		return true;
	}

}
