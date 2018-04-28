package me.thesquadmc.objects.logs;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class LogUser {

    private static final Map<UUID, LogUser> USERS = new HashMap<>();

    private final UUID uuid;
    private final String name;

    private final LinkedList<me.thesquadmc.objects.log.Log> logs = Lists.newLinkedList();

    public LogUser(OfflinePlayer player) {
        Preconditions.checkNotNull(player, "Cannot construct a LogUser from a null player");
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public LogUser(UUID uuid) {
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

    public LinkedList<me.thesquadmc.objects.log.Log> getLogs() {
        return logs;
    }

    public Player getPlayerOnline() {
        return Bukkit.getPlayer(uuid);
    }


    public static LogUser fromPlayer(OfflinePlayer player) {
        return (player != null) ? USERS.computeIfAbsent(player.getUniqueId(), LogUser::new) : null;
    }

    public static LogUser fromUUID(UUID player) {
        return (player != null) ? USERS.computeIfAbsent(player, LogUser::new) : null;
    }

    public static boolean isLoaded(OfflinePlayer player) {
        return player != null && USERS.containsKey(player.getUniqueId());
    }

    public static boolean isLoaded(UUID player) {
        return USERS.containsKey(player);
    }

    public static void unloadUser(LogUser user) {
        USERS.remove(user.uuid);
    }

    public static void unloadUser(OfflinePlayer player) {
        if (player == null) return;
        USERS.remove(player.getUniqueId());
    }

    public static void unloadUser(UUID player) {
        USERS.remove(player);
    }

    public static Collection<LogUser> getUsers() {
        return Collections.unmodifiableCollection(USERS.values());
    }

    public static void clearUsers() {
        USERS.clear();
    }

}