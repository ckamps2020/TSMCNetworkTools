package com.sgtcaze.nametagedit;

import com.sgtcaze.nametagedit.api.ChangeType;
import com.sgtcaze.nametagedit.api.data.GroupData;
import com.sgtcaze.nametagedit.api.data.INametag;
import com.sgtcaze.nametagedit.api.data.PlayerData;
import com.sgtcaze.nametagedit.storage.AbstractConfig;
import com.sgtcaze.nametagedit.storage.flatfile.FlatFileConfig;
import com.sgtcaze.nametagedit.utils.Configuration;
import com.sgtcaze.nametagedit.utils.UUIDFetcher;
import com.sgtcaze.nametagedit.utils.Utils;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NametagHandler implements Listener {

    public static boolean DISABLE_PUSH_ALL_TAGS = false;
    // Multiple threads access resources. We need to make sure we avoid concurrency issues.
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private boolean debug;

    private boolean tabListEnabled;
    private boolean longNametagsEnabled;

    private BukkitTask clearEmptyTeamTask;
    private BukkitTask refreshNametagTask;
    private AbstractConfig abstractConfig;

    private Configuration config;

    private List<GroupData> groupData = new ArrayList<>();

    private Map<UUID, PlayerData> playerData = new HashMap<>();

    private NametagEdit plugin;
    private NametagManager nametagManager;

    public NametagHandler(NametagEdit plugin, NametagManager nametagManager) {
        this.config = getCustomConfig(NetworkTools.getInstance());
        this.plugin = plugin;
        this.nametagManager = nametagManager;
        Bukkit.getPluginManager().registerEvents(this, NetworkTools.getInstance());

        // Apply config properties
        this.applyConfig();

        abstractConfig = new FlatFileConfig(plugin, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                abstractConfig.load();
            }
        }.runTaskAsynchronously(NetworkTools.getInstance());
    }

    /**
     * This function loads our custom config with comments, and includes changes
     */
    private Configuration getCustomConfig(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveDefaultConfig();

            Configuration newConfig = new Configuration(file);
            newConfig.reload(true);
            return newConfig;
        } else {
            Configuration oldConfig = new Configuration(file);
            oldConfig.reload(false);

            file.delete();
            plugin.saveDefaultConfig();

            Configuration newConfig = new Configuration(file);
            newConfig.reload(true);

            for (String key : oldConfig.getKeys(false)) {
                if (newConfig.contains(key)) {
                    newConfig.set(key, oldConfig.get(key));
                }
            }

            newConfig.save();
            return newConfig;
        }
    }

    /**
     * Cleans up any nametag data on the server to prevent memory leaks
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        nametagManager.reset(event.getPlayer().getName());
    }

    /**
     * Applies tags to a player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        nametagManager.sendTeams(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                abstractConfig.load(player, true);
            }
        }.runTaskLaterAsynchronously(NetworkTools.getInstance(), 1);
    }

    private void handleClear(UUID uuid, String player) {
        removePlayerData(uuid);
        nametagManager.reset(player);
        abstractConfig.clear(uuid, player);
    }

    public void clearMemoryData() {
        try {
            readWriteLock.writeLock().lock();
            groupData.clear();
            playerData.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removePlayerData(UUID uuid) {
        try {
            readWriteLock.writeLock().lock();
            playerData.remove(uuid);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void storePlayerData(UUID uuid, PlayerData data) {
        try {
            readWriteLock.writeLock().lock();
            playerData.put(uuid, data);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void assignGroupData(List<GroupData> groupData) {
        try {
            readWriteLock.writeLock().lock();
            this.groupData = groupData;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void assignData(List<GroupData> groupData, Map<UUID, PlayerData> playerData) {
        try {
            readWriteLock.writeLock().lock();
            this.groupData = groupData;
            this.playerData = playerData;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // ==========================================
    // Below are methods used by the API/Commands
    // ==========================================
    boolean debug() {
        return debug;
    }

    void toggleDebug() {
        debug = !debug;
        config.set("Debug", debug);
        config.save();
    }

    void toggleLongTags() {
        longNametagsEnabled = !longNametagsEnabled;
        config.set("Tablist.LongTags", longNametagsEnabled);
        config.save();
    }

    // =================================================
    // Below are methods that we have to be careful with
    // as they can be called from different threads
    // =================================================
    public PlayerData getPlayerData(Player player) {
        return player == null ? null : playerData.get(player.getUniqueId());
    }

    void addGroup(GroupData data) {
        abstractConfig.add(data);

        try {
            readWriteLock.writeLock().lock();
            groupData.add(data);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    void deleteGroup(GroupData data) {
        abstractConfig.delete(data);

        try {
            readWriteLock.writeLock().lock();
            groupData.remove(data);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public List<GroupData> getGroupData() {
        try {
            readWriteLock.writeLock().lock();
            return new ArrayList<>(groupData); // Create a copy instead of unmodifiable
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void setGroupData(List<GroupData> groupData) {
        this.groupData = groupData;
    }

    public GroupData getGroupData(String key) {
        for (GroupData groupData : getGroupData()) {
            if (groupData.getGroupName().equalsIgnoreCase(key)) {
                return groupData;
            }
        }

        return null;
    }

    /**
     * Replaces placeholders when a player tag is created.
     * Maxim and Clip's plugins are searched for, and input
     * is replaced. We use direct imports to avoid any problems!
     * (So don't change that)
     */
    public String formatWithPlaceholders(Player player, String input, boolean limitChars) {
        if (input == null) return "";
        if (player == null) return input;

        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            plugin.debug("Trying to use MVdWPlaceholderAPI for placeholders");
            if (be.maximvdw.placeholderapi.PlaceholderAPI.getLoadedPlaceholderCount() != 0) {
                input = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, input);
            }
        }

        // The string can become null again at this point. Add another check.
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.debug("Trying to use PlaceholderAPI for placeholders");
            input = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, input);
        }

        return Utils.format(input, limitChars);
    }

    private BukkitTask createTask(String path, BukkitTask existing, Runnable runnable) {
        if (existing != null) {
            existing.cancel();
        }

        if (config.getInt(path, -1) <= 0) return null;
        return Bukkit.getScheduler().runTaskTimer(NetworkTools.getInstance(), runnable, 0, 20 * config.getInt(path));
    }

    public void reload() {
        config.reload(true);
        applyConfig();
        nametagManager.reset();
        abstractConfig.reload();
    }

    private void applyConfig() {
        this.debug = config.getBoolean("Debug");
        this.tabListEnabled = config.getBoolean("Tablist.Enabled");
        this.longNametagsEnabled = config.getBoolean("Tablist.LongTags");
        DISABLE_PUSH_ALL_TAGS = config.getBoolean("DisablePush");

        clearEmptyTeamTask = createTask("ClearEmptyTeamsInterval", clearEmptyTeamTask, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte teams clear"));

        refreshNametagTask = createTask("RefreshInterval", refreshNametagTask, () -> {
            nametagManager.reset();
            applyTags();
        });
    }

    public Configuration getConfig() {
        return config;
    }

    public void applyTags() {
        if (!Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    applyTags();
                }
            }.runTask(NetworkTools.getInstance());
            return;
        }

        for (Player online : Utils.getOnline()) {
            if (online != null) {
                applyTagToPlayer(online, false);
            }
        }

        plugin.debug("Applied tags to all online players.");
    }

    public void applyTagToPlayer(final Player player, final boolean loggedIn) {
        // If on the primary thread, run async
        if (Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    applyTagToPlayer(player, loggedIn);
                }
            }.runTaskAsynchronously(NetworkTools.getInstance());
            return;
        }

        INametag tempNametag = getPlayerData(player);
        if (tempNametag == null) {
            for (GroupData group : getGroupData()) {
                if (player.hasPermission(group.getBukkitPermission())) {
                    tempNametag = group;
                    break;
                }
            }
        }

        if (tempNametag == null) return;
        plugin.debug("Applying " + (tempNametag.isPlayerTag() ? "PlayerTag" : "GroupTag") + " to " + player.getName());

        final INametag nametag = tempNametag;
        new BukkitRunnable() {
            @Override
            public void run() {
                nametagManager.setNametag(player.getName(), formatWithPlaceholders(player, nametag.getPrefix(), true),
                        formatWithPlaceholders(player, nametag.getSuffix(), true), nametag.getSortPriority());
                // If the TabList is disabled...
                if (!tabListEnabled) {
                    // apply the default white username to the player.
                    player.setPlayerListName(Utils.format("&f" + player.getPlayerListName()));
                } else {
                    if (longNametagsEnabled) {
                        player.setPlayerListName(formatWithPlaceholders(player, nametag.getPrefix() + player.getName() + nametag.getSuffix(), false));
                    } else {
                        player.setPlayerListName(null);
                    }
                }
            }
        }.runTask(NetworkTools.getInstance());
    }

    void clear(final CommandSender sender, final String player) {
        Player target = Bukkit.getPlayerExact(player);
        if (target != null) {
            handleClear(target.getUniqueId(), player);
            return;
        }

        UUIDFetcher.lookupUUID(player, NetworkTools.getInstance(), uuid -> {
            if (uuid == null) {
                NametagMessages.UUID_LOOKUP_FAILED.send(sender);
            } else {
                handleClear(uuid, player);
            }
        });
    }

    void save(CommandSender sender, boolean playerTag, String key, int priority) {
        if (playerTag) {
            Player player = Bukkit.getPlayerExact(key);

            PlayerData data = getPlayerData(player);
            if (data == null) {
                abstractConfig.savePriority(true, key, priority);
                return;
            }

            data.setSortPriority(priority);
            abstractConfig.save(data);
        } else {
            GroupData groupData = getGroupData(key);

            if (groupData == null) {
                sender.sendMessage(ChatColor.RED + "Group " + key + " does not exist!");
                return;
            }

            groupData.setSortPriority(priority);
            abstractConfig.save(groupData);
        }
    }

    void save(final CommandSender sender, String targetName, ChangeType changeType, String value) {
        Player player = Bukkit.getPlayerExact(targetName);

        PlayerData data = getPlayerData(player);
        if (data == null) {
            data = new PlayerData(targetName, null, "", "", -1);
            if (player != null) {
                storePlayerData(player.getUniqueId(), data);
            }
        }

        if (changeType == ChangeType.PREFIX) {
            data.setPrefix(value);
        } else {
            data.setSuffix(value);
        }

        if (player != null) {
            applyTagToPlayer(player, false);
            data.setUuid(player.getUniqueId());
            abstractConfig.save(data);
            return;
        }

        final PlayerData finalData = data;
        UUIDFetcher.lookupUUID(targetName, NetworkTools.getInstance(), uuid -> {
            if (uuid == null) {
                NametagMessages.UUID_LOOKUP_FAILED.send(sender);
            } else {
                storePlayerData(uuid, finalData);
                finalData.setUuid(uuid);
                abstractConfig.save(finalData);
            }
        });
    }

    public boolean isLongNametagsEnabled() {
        return longNametagsEnabled;
    }

    public AbstractConfig getAbstractConfig() {
        return abstractConfig;
    }

    public NametagEdit getPlugin() {
        return plugin;
    }
}