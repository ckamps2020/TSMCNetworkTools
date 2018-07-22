package com.thesquadmc.networktools.chat;

import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class ChatMessage {

    private final Player player;
    private final String message;
    private final String server;
    private final ChatType type;
    private final Date timestamp;
    private final UUID target;

    public ChatMessage(Player player, String message, String server, ChatType type, Date timestamp, UUID target) {
        this.player = player;
        this.message = message;
        this.server = server;
        this.type = type;
        this.timestamp = timestamp;

        // Ensure we have a target if it is a private message
        if (type == ChatType.PRIVATE && target == null) {
            throw new IllegalStateException("Cannot be a private message with a null target");
        }

        this.target = target;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public String getServer() {
        return server;
    }

    public ChatType getType() {
        return type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public UUID getTarget() {
        return target;
    }

    Document toDocument() {
        Document document = new Document()
                .append("message", message)
                .append("time", timestamp)
                .append("server", server);

        if (target != null) {
            document.append("target", target);
        }

        return document;
    }

    public enum ChatType {
        PUBLIC,
        COMMAND,
        PRIVATE
    }
}
