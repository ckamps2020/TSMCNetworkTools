package me.thesquadmc.utils;

public enum RedisChannels {

	ANNOUNCEMENT("ANNOUNCEMENT"),
	STAFFCHAT("STAFFCHAT"),
	ADMINCHAT("ADMINCHAT"),
	MANAGERCHAT("MANAGERCHAT"),
	TELL_PLAYER("TELL_PLAYER"),
	INSTANCE("INSTANCE"),
	FIND("FIND"),
	FOUND("FOUND"),
	REQUEST_LIST("REQUEST_LIST"),
	RETURN_REQUEST_LIST("RETURN_REQUEST_LIST"),
	;

	private String channelName;

	RedisChannels(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelName() { return channelName; }

}
