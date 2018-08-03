package com.thesquadmc.networktools.utils.enums;

public enum RedisChannels {

    ANNOUNCEMENT("ANNOUNCEMENT"),
    MESSAGE("MESSAGE"),
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
    MONITOR_REQUEST("MONITOR_REQUEST"),
    MONITOR_RETURN("MONITOR_RETURN"),
    MONITOR_INFO("MONITOR_INFO"),
    PROXY_REQUEST("PROXY_REQUEST"),
    PROXY_RETURN("PROXY_RETURN"),
    HEARTBEAT("HEARTBEAT"),

    FRIEND_ADD("FRIEND_ADD"),
    FRIEND_REMOVE_OUTBOUND("FRIEND_REMOVE_OUTBOUND"),
    FRIEND_REMOVE_INBOUND("FRIEND_REMOVE_INBOUND"),
    FRIEND_CHAT("FRIEND_CHAT"),

    FRIEND_CHECK_REQUEST("FRIEND_CHECK_REQUEST"),
    FRIEND_RETURN_REQUEST("FRIEND_RETURN_REQUEST"),

    LOGIN("LOGIN"),
    LEAVE("LEAVE"),

    TRANSPORT("TRANSPORT"),

    SERVER_STATE("SERVER_STATE"),

    REQUEST_SERVER("REQUEST_SERVER"),
    PLAYER_COUNT("PLAYER_COUNT"),
    RETURN_SERVER("RETURN_SERVER"),
    STARTUP_REQUEST("STARTUP_REQUEST"),

    SLACK_STAFFCHAT("SLACK_STAFFCHAT"),
    MOTD("MOTD"),
    DISCORD_STAFFCHAT_DISCORD("DISCORD_STAFFCHAT_DISCORD"),
    DISCORD_STAFFCHAT_SERVER("DISCORD_STAFFCHAT_SERVER"),

    NOTES("NOTES")
    ;

    private String channelName;

    RedisChannels(String channelName) {
        this.channelName = channelName;
    }

    @Deprecated
    public String getChannelName() {
        return channelName;
    }

    public String getName() {
        return channelName;
    }

}
