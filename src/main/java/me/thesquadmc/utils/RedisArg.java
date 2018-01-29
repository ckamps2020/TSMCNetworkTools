package me.thesquadmc.utils;

public enum RedisArg {

	SERVER("SERVER"),
	PLAYER("PLAYER"),
	MESSAGE("MESSAGE"),
	LOGIN("LOGIN"),
	PLAYER_RECEIVER("PLAYER_RECEIVER"),
	STOP("STOP"),
	DELETE("DELETE"),
	CREATE("CREATE"),
	START("START"),
	MAINTENANCE("MAINTENANCE"),
	SERVER_TYPE("SERVER_TYPE"),
	ORIGIN_SERVER("ORIGIN_SERVER"),
	REDIS_ARG("REDIS_ARG"),
	ORIGIN_PLAYER("ORIGIN_PLAYER"),
	UUID("UUID"),
	;

	private String arg;

	RedisArg(String arg) {
		this.arg = arg;
	}

	public String getArg() { return arg; }

}
