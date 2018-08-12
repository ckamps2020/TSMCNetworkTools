package com.thesquadmc.networktools.commands;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.objects.logging.ChangeLog;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class ChangeLogCommand {

    private final MongoCollection<Document> logCollection;
    private final TreeSet<ChangeLog> logs = Sets.newTreeSet(Comparator.comparingLong(ChangeLog::getTimestamp));

    public ChangeLogCommand(NetworkTools networkTools) {
        logCollection = networkTools.getMongoManager().getMongoDatabase().getCollection("changelog");

        for (Document document : logCollection.find().limit(10).sort(Sorts.descending("timestamp"))) {
            logs.add(ChangeLog.fromDocument(document));
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(networkTools, () -> {
            logs.clear();

            for (Document document : logCollection.find().limit(10).sort(Sorts.descending("timestamp"))) {
                logs.add(ChangeLog.fromDocument(document));
            }
        }, 0L, 20 * 30);

    }

    @Command(name = {"changelog", "changes"})
    public void on(CommandArgs args) {
        Iterator<ChangeLog> iterator = logs.descendingIterator();

        while (iterator.hasNext()) {
            ChangeLog log = iterator.next();
            args.getSender().sendMessage(log.getMessage());
        }
    }
}
