package me.thesquadmc.utils;

public enum Rank {

	TRAINEE("TRAINEE", 1),
	HELPER("HELPER", 2),
	MOD("MOD", 3),
	SRMOD("SRMOD", 4),
	YOUTUBE("YOUTUBE", 5),
	ADMIN("ADMIN", 6),
	MANAGER("MANAGER", 7),
	DEVELOPER("DEVELOPER", 8),
	OWNER("OWNER", 9),
	;

	private String name;
	private int priority;

	Rank(String name, int priority) {
		this.name = name;
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

}
