package me.thesquadmc.networking.mongo;

import com.mongodb.client.MongoCollection;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class MongoUserDatabase implements UserDatabase {

    private final Mongo plugin;
    private final MongoCollection<Document> users;

    public MongoUserDatabase(Mongo plugin) {
        this.plugin = plugin;
        this.users = plugin.getMongoDatabase().getCollection(UserDatabase.USER_DATABASE, Document.class);

        System.out.println(users);
    }

    @Override
    public CompletableFuture<TSMCUser> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = users.find(eq("_id", uuid)).first();

            if (document == null) {
                return  TSMCUser.fromUUID(uuid);
            }

            return TSMCUser.fromDocument(document);
        }, Multithreading.POOL);
    }

    @Override
    public CompletableFuture<TSMCUser> getUser(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = users.find(eq(UserDatabase.NAME, name)).first();

            if (document == null) {
                return null;
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
