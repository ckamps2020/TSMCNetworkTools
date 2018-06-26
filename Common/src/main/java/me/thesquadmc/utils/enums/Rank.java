package me.thesquadmc.utils.enums;

import me.thesquadmc.utils.msgs.CC;

public enum Rank {

	PLAYER("PLAYER", 1, CC.translate("&7")),
	TRAINEE("TRAINEE", 2, CC.translate("&a&lTRAINEE")),
	HELPER("HELPER", 3, CC.translate("&b&lCHAT-MOD")),
	MOD("MOD", 4, CC.translate("&5&lMOD")),
	SRMOD("SRMOD", 5, CC.translate("&d&lSR-MOD")),
	YOUTUBE("YOUTUBE", 6, CC.translate("&c&lYOUTUBE")),
	ADMIN("ADMIN", 7, CC.translate("&c&lADMIN")),
	MANAGER("MANAGER", 8, CC.translate("&c&lMANAGER")),
	DEVELOPER("DEVELOPER", 9, CC.translate("&c&lDEVELOPER")),
	OWNER("OWNER", 10, CC.translate("&4&lOWNER")),
	;

	private String name;
	private int priority;
	private String tablistPrefix;

	Rank(String name, int priority, String tablistPrefix) {
		this.name = name;
		this.priority = priority;
		this.tablistPrefix = tablistPrefix;
	}

	public String getTablistPrefix() {
		return tablistPrefix;
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

}
