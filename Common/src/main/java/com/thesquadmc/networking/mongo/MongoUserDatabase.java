package com.thesquadmc.networking.mongo;

import com.mongodb.client.MongoCollection;
import com.thesquadmc.NetworkTools;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.server.Multithreading;
import org.bson.Document;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class MongoUserDatabase implements UserDatabase {

    private final MongoCollection<Document> users;

    public MongoUserDatabase(MongoManager plugin) {
        this.users = plugin.getMongoDatabase().getCollection(USER_DATABASE, Document.class);
    }

    @Override
    public CompletableFuture<TSMCUser> getUser(UUID uuid) {
        if (TSMCUser.isLoaded(uuid)) {
            return CompletableFuture.completedFuture(TSMCUser.fromUUID(uuid));
        }

        return CompletableFuture.supplyAsync(() -> {
            Document document = users.find(eq("_id", uuid)).first();

            if (document == null) {
                NetworkTools.getInstance().getLogger().info(MessageFormat.format("{0} had no document, fresh user!", uuid));
                return TSMCUser.fromUUID(uuid);
            }

            return TSMCUser.fromDocument(document);
        }, Multithreading.POOL);
    }

    @Override
    public CompletableFuture<Void> saveUser(TSMCUser user) {
        return CompletableFuture.runAsync(() -> {
            Document document = users.find(eq("_id", user.getUUID())).first();

            if (document != null) {
                users.findOneAndReplace(eq("_id", user.getUUID()), TSMCUser.toDocument(user));

            } else {
                users.insertOne(TSMCUser.toDocument(user));
            }
        });
    }
}
