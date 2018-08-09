package com.thesquadmc.networktools.chat.event;

import com.thesquadmc.networktools.utils.handlers.BaseEvent;

public class PlayerMessageEvent extends BaseEvent {

    // Using strings as a target
    // can be on another server
    private final String sender;
    private final String target;
    private final String message;

    public PlayerMessageEvent(String sender, String target, String message) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }
}
