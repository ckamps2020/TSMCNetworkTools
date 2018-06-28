package me.thesquadmc.networking.mongo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.thesquadmc.objects.TSMCUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserDatabase {

    String USER_DATABASE = "users";

    String UUID = "uuid";
    String NAME = "name";

    String PREVIOUS_NAMES = "previous_names";
    String FRIENDS = "friends";
    String REQUESTS = "requests";
    String NOTES = "notes";

    String VANISHED = "vanished";
    String YT_VANISHED = "ytVanished";
    String XRAY = "xray";
    String MONITOR = "monitor";
    String REPORTS = "reports";
    String FORCEFIELD = "forcefield";
    String NICKNAMED = "nicknamed";

    String SKIN_KEY = "skinKey";
    String SIGNATURE = "signature";

    String TIMESTAMP = "timestamp";

    String NOTE_CREATOR = "creator";
    String NOTE_CREATOR_NAME = "creatorName";
    String NOTE_MESSAGE = "message";

    /**
     * Loads a user from the database using
     * the player's uuid. If a player was
     * not found, it will create a {@link TSMCUser}
     * instance
     *
     * @param uuid uuid of the player to load
     * @return the user
     */
    CompletableFuture<TSMCUser> getUser(UUID uuid);

    /**
     * Loads a user from the database using
     * the player's name. If a player was not
     * found, it will return null
     *
     * @param name name of the player to load
     * @return the user
     */
    CompletableFuture<TSMCUser> getUser(String name);

    /**
     * Saves a user to the database
     *
     * @param user user to save
     * @return whether save was successful
     */
    CompletableFuture<Void> saveUser(TSMCUser user);
}
