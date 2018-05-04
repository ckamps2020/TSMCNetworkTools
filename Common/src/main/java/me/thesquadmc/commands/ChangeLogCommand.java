package me.thesquadmc.commands;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import me.thesquadmc.Main;
import me.thesquadmc.objects.ChangeLog;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public final class ChangeLogCommand {

    private final MongoCollection<Document> logCollection;
    private final TreeSet<ChangeLog> logs = Sets.newTreeSet(Comparator.comparingLong(ChangeLog::getTimestamp));

    public ChangeLogCommand(Main main) {
        logCollection = main.getMongo().getMongoDatabase().getCollection("changelog");

        for (Document document : logCollection.find().limit(10).sort(Sorts.descending("timestamp"))) {
            logs.add(ChangeLog.fromDocument(document));
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            logs.clear();

            for (Document document : logCollection.find().limit(10).sort(Sorts.descending("timestamp"))) {
                logs.add(ChangeLog.fromDocument(document));
            }
        }, 0L, 20 * 30);
    }

    @Command(name = "changelog", aliases = {"changes"})
    public void on(CommandArgs args) {
        Iterator<ChangeLog> iterator = logs.descendingIterator();

        while (iterator.hasNext()) {
            ChangeLog log = iterator.next();
            args.getSender().sendMessage(log.getMessage());
        }
    }
}
