package me.thesquadmc.objects.logging.chatlogs;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class LogUser {

    private static final Map<UUID, LogUser> USERS = new HashMap<>();

    private final UUID uuid;
    private final String name;

    private final LinkedList<Log> logs = Lists.newLinkedList();

    public LogUser(OfflinePlayer player) {
        Preconditions.checkNotNull(player, "Cannot construct a LogUser from a null player");
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    private LogUser(UUID uuid) {
        Preconditions.checkNotNull(uuid, "Cannot construct a LogUser from a null UUID");
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public LinkedList<Log> getLogs() {
        return logs;
    }

    public static LogUser fromPlayer(OfflinePlayer player) {
        return (player != null) ? USERS.computeIfAbsent(player.getUniqueId(), LogUser::new) : null;
    }

    public static Collection<LogUser> getUsers() {
        return Collections.unmodifiableCollection(USERS.values());
    }

    /**
     * @return Compiles a list of {@link Document} of chat logs by each user
     */
    public static List<Document> toDocuments() {
        List<Document> updateDocuments = Lists.newArrayList();

        USERS.forEach((uuid, logUser) -> {
            logUser.getLogs().forEach(log -> updateDocuments.add(new Document("uuid", uuid)
                    .append("name", logUser.getName())
                    .append("type", log.getType().name())
                    .append("time", log.getTimestamp())
                    .append("server", log.getServer())
                    .append("message", log.getMessage())));

            logUser.getLogs().clear();
        });

        return updateDocuments;
    }
}