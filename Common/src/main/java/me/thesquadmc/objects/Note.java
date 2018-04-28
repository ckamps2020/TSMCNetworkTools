package me.thesquadmc.objects;

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

    public Note(Player creator, String note) {
        this.creator = creator.getUniqueId();
        this.creatorName = creator.getName();
        this.timestamp = System.currentTimeMillis();
        this.note = note;
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
}
