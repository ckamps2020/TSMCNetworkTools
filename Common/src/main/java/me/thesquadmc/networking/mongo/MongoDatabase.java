package me.thesquadmc.networking.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.sun.org.apache.xpath.internal.operations.Bool;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class MongoDatabase implements Database {

    private final Mongo plugin;
    private final MongoCollection<Document> users;

    public MongoDatabase(Mongo plugin) {
        this.plugin = plugin;
        this.users = plugin.getMongoDatabase().getCollection(Database.USER_DATABASE, Document.class);

        System.out.println(users);
    }

    @Override
    public CompletableFuture<TSMCUser> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = users.find(eq("_id", uuid)).first();
            System.out.println(document);

            if (document == null) {
                System.out.println("is null");
                return  TSMCUser.fromUUID(uuid);
            }

            return TSMCUser.fromDocument(document);
        }, Multithreading.POOL);
    }

    @Override
    public CompletableFuture<Void> saveUser(TSMCUser user) {
        return CompletableFuture.runAsync(() -> {
            Document prisonUser = users.find(eq("_id", user.getUuid())).first();

            if (prisonUser != null) {
                users.replaceOne(eq("_id", user.getUuid()), TSMCUser.toDocument(user));

            } else {
                users.insertOne(TSMCUser.toDocument(user));
            }
        });
    }
}
