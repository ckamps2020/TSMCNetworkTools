package me.thesquadmc.objects;

import me.thesquadmc.networking.mongo.Database;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Note {

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
    private final long timestamp;

    /**
     * The actual note message itself
     */
    private final String note;

    public Note(UUID creator, String creatorName, long timestamp, String note) {
        this.creator = creator;
        this.creatorName = creatorName;
        this.timestamp = timestamp;
        this.note = note;
    }

    public Note(Player player, String note) {
        this(player.getUniqueId(), player.getName(), System.currentTimeMillis(), note);
    }

    public UUID getCreator() {
        return creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNote() {
        return note;
    }

    public static Document toDocument(Note note) {
        return null;
    }

    public static Note fromDocument(Document document) {
        return new Note(
                document.get(Database.NOTE_CREATOR, UUID.class),
                document.getString(Database.NOTE_CREATOR_NAME),
                document.getLong(Database.TIMESTAMP),
                document.getString(Database.NOTE_MESSAGE)
        );
    }
}
