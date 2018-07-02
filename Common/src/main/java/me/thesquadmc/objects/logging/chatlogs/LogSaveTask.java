package me.thesquadmc.objects.logging.chatlogs;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import me.thesquadmc.Main;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LogSaveTask extends BukkitRunnable {

    private final MongoCollection<Document> logCollection;

    public LogSaveTask(Main plugin) {
        this.logCollection = plugin.getMongo().getMongoDatabase().getCollection("playerLogs");

        logCollection.createIndex(Indexes.descending("uuid"), new IndexOptions().unique(false));
    }

    @Override
    public void run() {
        List<Document> updateDocuments = LogUser.toDocuments();

        Multithreading.runAsync(() -> {
            if (updateDocuments.isEmpty()) {
                return;
            }

            logCollection.insertMany(updateDocuments);
        });
    }
}