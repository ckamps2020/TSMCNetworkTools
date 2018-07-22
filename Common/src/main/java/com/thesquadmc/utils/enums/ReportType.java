package com.thesquadmc.utils.enums;

public enum ReportType {

	KILLAURA("KILLAURA"),
	REACH("REACH"),
	FLY("FLY"),
	GLIDE("GLIDE"),
	AUTOCLICKER("AUTOCLICKER"),
	SPEED("SPEED"),
	ANTIKNOCKBACK("ANTIKNOCKBACK"),
	JESUS("JESUS"),
	DOLPHIN("DOLPHIN"),
	CRITICALS("CRITICALS"),
	VCLIP("VCLIP"),
	HCLIP("HCLIP"),
	NOFALL("NOFALL"),
	PHASE("PHASE"),
	SNEAK("SNEAK"),
	FASTBOW("FASTBOW"),
	;

	private String cheatType;

	ReportType(String cheatType) {
		this.cheatType = cheatType;
	}

	public String getCheatType() {
		return cheatType;
	}
}
