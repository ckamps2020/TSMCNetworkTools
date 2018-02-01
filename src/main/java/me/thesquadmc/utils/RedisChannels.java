package me.thesquadmc.utils;

public enum RedisChannels {

	ANNOUNCEMENT("ANNOUNCEMENT"),
	STAFFCHAT("STAFFCHAT"),
	ADMINCHAT("ADMINCHAT"),
	MANAGERCHAT("MANAGERCHAT"),
	TELL_PLAYER("TELL_PLAYER"),
	FIND("FIND"),
	FOUND("FOUND"),
	REQUEST_LIST("REQUEST_LIST"),
	RETURN_REQUEST_LIST("RETURN_REQUEST_LIST"),
	STOP("STOP"),
	WHITELIST("WHITELIST"),
	WHITELIST_ADD("WHITELIST_ADD"),
	WHITELIST_REMOVE("WHITELIST_REMOVE"),
	REPORTS("REPORTS"),
	CLOSED_REPORTS("CLOSED_REPORTS"),
	;

	private String channelName;

	RedisChannels(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelName() { return channelName; }

}
