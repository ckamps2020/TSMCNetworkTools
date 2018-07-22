package com.thesquadmc.networking.mongo;

import com.thesquadmc.player.TSMCUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserDatabase {

    String USER_DATABASE = "users";

    String UUID = "uuid";
    String NAME = "name";
    String NICKNAME = "name";

    String PREVIOUS_NAMES = "previous_names";
    String FRIENDS = "friends";
    String REQUESTS = "requests";
    String NOTES = "notes";
    String SETTINGS = "settings";

    String SKIN_KEY = "skinKey";
    String SIGNATURE = "signature";

    String TIMESTAMP = "timestamp";

    String IPS = "ips";
    String IP = "ip";
    String FIRST_JOINED = "first_joined";
    String LAST_JOINED = "last_joined";
    String COUNT = "count";

    String LAST_MESSAGER = "last_messager";
    String CREATOR = "creator";
    String CREATOR_NAME = "creator_name";
    String MESSAGE = "message";

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
     * Saves a user to the database
     *
     * @param user user to save
     * @return whether save was successful
     */
    CompletableFuture<Void> saveUser(TSMCUser user);
}
