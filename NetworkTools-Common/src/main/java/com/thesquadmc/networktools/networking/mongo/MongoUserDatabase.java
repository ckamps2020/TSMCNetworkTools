package com.thesquadmc.networktools.networking.mongo;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class MongoUserDatabase implements UserDatabase {

    private final MongoCollection<Document> users;

    public MongoUserDatabase(MongoManager plugin) {
        this.users = plugin.getMongoDatabase().getCollection(USER_DATABASE, Document.class);
        users.createIndex(new Document("name", 1));

        Bukkit.getScheduler().runTaskTimerAsynchronously(NetworkTools.getInstance(), () -> {
            List<ReplaceOneModel<Document>> bulk = Lists.newArrayList();

            for (TSMCUser user : TSMCUser.getUsers()) {
                Document document = TSMCUser.toDocument(user);

                bulk.add(new ReplaceOneModel<>(
                        Filters.eq("_id", user.getUUID()),
                        document,
                        new UpdateOptions().upsert(true)
                ));
            }

            if (bulk.size() > 0) {
                BulkWriteResult result = users.bulkWrite(bulk);
                if (result.getModifiedCount() <= 0) {
                    NetworkTools.getInstance().getLogger().severe(MessageFormat.format("Failed insert for {0} messages: {1}", bulk.size(), result));

                }
            }
        }, 0, 5 * 60 * 20);
    }

    @Override
    public CompletableFuture<TSMCUser> getUser(UUID uuid) {
        if (uuid == null) {
            return CompletableFuture.completedFuture(null);
        }

        if (TSMCUser.isLoaded(uuid)) {
            return CompletableFuture.completedFuture(TSMCUser.fromUUID(uuid));
        }

        return CompletableFuture.supplyAsync(() -> {
            Document document = users.find(eq("_id", uuid)).first();

            if (document == null) {
                NetworkTools.getInstance().getLogger().info(MessageFormat.format("{0} had no document, fresh user!", uuid));

                TSMCUser user = TSMCUser.fromUUID(uuid);
                user.setFirstJoin(new Date());

                return TSMCUser.fromUUID(uuid);
            }

            return TSMCUser.fromDocument(document);
        }, Multithreading.POOL);
    }

    @Override
    public CompletableFuture<TSMCUser> getUser(String name) {
        if (Bukkit.getPlayer(name) != null) {
            return CompletableFuture.completedFuture(TSMCUser.fromPlayer(Bukkit.getPlayer(name)));
        }

        return CompletableFuture.supplyAsync(() -> {
            Document document = users.find(eq("name", name)).first();

            if (document == null) {
                return null;
            }

            return TSMCUser.fromDocument(document);
        });
    }

    @Override
    public CompletableFuture<Void> saveUser(TSMCUser user) {
        return CompletableFuture.runAsync(() -> {
            Preconditions.checkNotNull(user.getUUID(), "uuid cannot be null!");
            Preconditions.checkNotNull(user.getName(), "name cannot be null!");
            // Document document = users.find(eq("_id", user.getUUID())).first();

            //if (document != null) {
            users.replaceOne(eq("_id", user.getUUID()), TSMCUser.toDocument(user)
                    , new UpdateOptions().upsert(true));

            //} else {
            //  users.insertOne(TSMCUser.toDocument(user));
            //}
        });
    }
}
