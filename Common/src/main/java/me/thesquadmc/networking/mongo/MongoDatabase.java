package me.thesquadmc.networking.mongo;

import com.mongodb.client.MongoCollection;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.mongodb.client.model.Filters.eq;

public class MongoDatabase implements Database {

    private final Mongo plugin;
    private final MongoCollection<Document> users;

    public MongoDatabase(Mongo plugin) {
        this.plugin = plugin;
        this.users = plugin.getMongoDatabase().getCollection(Mongo.USER_DATABASE);

    }

    @Override
    public CompletableFuture<TSMCUser> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(new Supplier<TSMCUser>() {
            @Override
            public TSMCUser get() {
                Document document = users.find(eq(Mongo.UUID, uuid)).first();

                if (document == null) {
                    return  TSMCUser.fromUUID(uuid);
                }


                return null;
            }
        }, Multithreading.POOL);
    }

    @Override
    public CompletableFuture<Boolean> saveUser(TSMCUser user) {
        return null;
    }
}
