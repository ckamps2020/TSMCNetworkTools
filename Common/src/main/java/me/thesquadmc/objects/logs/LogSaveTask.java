package me.thesquadmc.objects.log;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import me.thesquadmc.Main;
import me.thesquadmc.objects.logs.LogUser;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LogSaveTask extends BukkitRunnable {

    private final MongoCollection<Document> logCollection;

    public LogSaveTask(Main plugin) {
        this.logCollection = plugin.getMongo().getMongoDatabase().getCollection("logs");

        logCollection.createIndex(Indexes.descending("uuid"), new IndexOptions().unique(false));
    }

    @Override
    public void run() {
        List<Document> updateDocuments = Lists.newArrayList();

        for (LogUser user : LogUser.getUsers()) {
            for (me.thesquadmc.objects.log.Log log : user.getLogs()) {
                updateDocuments.add(new Document("uuid", user.getUuid())
                        .append("name", user.getName())
                        .append("type", log.getType())
                        .append("time", log.getTimestamp())
                        .append("server", log.getServer())
                        .append("message", log.getMessage()));
            }

            user.getLogs().clear();
        }

        Multithreading.runAsync(() -> {
            if (updateDocuments.isEmpty()) {
                return;
            }

            logCollection.insertMany(updateDocuments);
        });
    }
}