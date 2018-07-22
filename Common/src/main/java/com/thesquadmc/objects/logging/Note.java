package com.thesquadmc.objects.logging;

import com.thesquadmc.networking.mongo.UserDatabase;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class Note {

    /**
     * An alphanumeric identifier to
     * index each note
     */
    private final String identifier;

    /**
     * The UUID of the Staff member
     * that created this Note
     */
    private final UUID creator;

    /**
     * The name of the Staff member
     * that created this Note
     */
    private final String creatorName;

    /**
     * The time that this Note was
     * created
     */
    private final Date timestamp;

    /**
     * The actual note message itself
     */
    private final String note;

    public Note(String identifier, UUID creator, String creatorName, Date timestamp, String note) {
        this.identifier = identifier;
        this.creator = creator;
        this.creatorName = creatorName;
        this.timestamp = timestamp;
        this.note = note;
    }

    public Note(String identifier, Player player, String note) {
        this(identifier, player.getUniqueId(), player.getName(), new Date(), note);
    }

    public static Note fromDocument(Document document) {
        return new Note(
                document.getString("_id"),
                document.get(UserDatabase.CREATOR, UUID.class),
                document.getString(UserDatabase.CREATOR_NAME),
                document.getDate(UserDatabase.TIMESTAMP),
                document.getString(UserDatabase.MESSAGE)
        );
    }

    public UUID getCreator() {
        return creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getNote() {
        return note;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
