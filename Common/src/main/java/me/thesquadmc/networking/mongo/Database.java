package me.thesquadmc.networking.mongo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.thesquadmc.objects.TSMCUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    /**
     * Loads a user from the database
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
    CompletableFuture<Boolean> saveUser(TSMCUser user);
}
