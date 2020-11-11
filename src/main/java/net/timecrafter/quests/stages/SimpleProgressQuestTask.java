package net.timecrafter.quests.stages;

import java.util.HashSet;
import java.util.Set;
import net.timecrafter.quests.party.QuestParty;

public abstract class SimpleProgressQuestTask implements QuestTask {

	private final Set<QuestParty> partiesOnQuest = new HashSet<>();

	@Override
	public void execute(QuestParty party) {
		partiesOnQuest.add(party);
	}

	@Override
	public void cancel(QuestParty party) {
		partiesOnQuest.remove(party);
	}

	@Override
	public void complete(QuestParty party) {
		partiesOnQuest.remove(party);
	}

	@Override
	public boolean isAvailable(QuestParty party) {
		return !partiesOnQuest.contains(party);
	}

	@Override
	public int getProgressDenominator(QuestParty party) {
		return party.getPartyMembers().size();
	}

}
