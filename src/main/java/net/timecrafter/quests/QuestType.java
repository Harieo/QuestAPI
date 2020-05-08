package net.timecrafter.quests;

public enum QuestType {

	MAIN("Main"),
	SIDE("Side"),
	TUTORIAL("Tutorial"),
	OTHER("Misc.");

	private final String humanName;

	QuestType(String humanName) {
		this.humanName = humanName + " Quest";
	}

	public String getHumanName() {
		return humanName;
	}

}
