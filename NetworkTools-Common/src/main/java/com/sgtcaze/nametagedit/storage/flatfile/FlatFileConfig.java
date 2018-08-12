package com.sgtcaze.nametagedit.storage.flatfile;

import com.sgtcaze.nametagedit.NametagEdit;
import com.sgtcaze.nametagedit.NametagHandler;
import com.sgtcaze.nametagedit.api.data.GroupData;
import com.sgtcaze.nametagedit.api.data.PlayerData;
import com.sgtcaze.nametagedit.storage.AbstractConfig;
import com.sgtcaze.nametagedit.utils.UUIDFetcher;
import com.sgtcaze.nametagedit.utils.Utils;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlatFileConfig implements AbstractConfig {

    private File groupsFile;
    private File playersFile;

    private YamlConfiguration groups;
    private YamlConfiguration players;

    private NametagEdit plugin;
    private NametagHandler handler;

    public FlatFileConfig(NametagEdit plugin, NametagHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @Override
    public void load() {
        groupsFile = new File(NetworkTools.getInstance().getDataFolder(), "groups.yml");
        groups = Utils.getConfig(groupsFile, "groups.yml", NetworkTools.getInstance());
        playersFile = new File(NetworkTools.getInstance().getDataFolder(), "players.yml");
        players = Utils.getConfig(playersFile, "players.yml", NetworkTools.getInstance());

        loadGroups();
        loadPlayers();

        new BukkitRunnable() {
            @Override
            public void run() {
                handler.applyTags();
            }
        }.runTask(NetworkTools.getInstance());
    }

    @Override
    public void reload() {
        handler.clearMemoryData();

        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskAsynchronously(NetworkTools.getInstance());
    }

    @Override
    public void shutdown() {
        // NOTE: Nothing to do
    }

    @Override
    public void load(Player player, boolean loggedIn) {
        loadPlayerTag(player);
        plugin.getHandler().applyTagToPlayer(player, loggedIn);
    }

    @Override
    public void save(PlayerData... data) {
        for (PlayerData playerData : data) {
            UUID uuid = playerData.getUuid();
            String name = playerData.getName();
            players.set("Players." + uuid + ".Name", name);
            players.set("Players." + uuid + ".Prefix", Utils.deformat(playerData.getPrefix()));
            players.set("Players." + uuid + ".Suffix", Utils.deformat(playerData.getSuffix()));
            players.set("Players." + uuid + ".SortPriority", playerData.getSortPriority());
        }

        save(players, playersFile);
    }

    @Override
    public void save(final GroupData... data) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (GroupData groupData : data) {
                    storeGroup(groupData);
                }

                save(groups, groupsFile);
            }
        }.runTaskAsynchronously(NetworkTools.getInstance());
    }

    @Override
    public void savePriority(boolean playerTag, String key, final int priority) {
        if (playerTag) {
            final Player target = Bukkit.getPlayerExact(key);
            if (target != null) {
                if (players.contains("Players." + target.getUniqueId().toString())) {
                    players.set("Players." + target.getUniqueId().toString(), priority);
                    save(players, playersFile);
                }
                return;
            }

            UUIDFetcher.lookupUUID(key, NetworkTools.getInstance(), uuid -> {
                if (players.contains("Players." + uuid.toString())) {
                    players.set("Players." + uuid.toString(), priority);
                    save(players, playersFile);
                }
            });
        }
    }

    @Override
    public void delete(GroupData groupData) {
        groups.set("Groups." + groupData.getGroupName(), null);
        save(groups, groupsFile);
    }

    @Override
    public void add(GroupData groupData) {
        // NOTE: Nothing to do
    }

    @Override
    public void clear(UUID uuid, String targetName) {
        handler.removePlayerData(uuid);
        players.set("Players." + uuid.toString(), null);
        save(players, playersFile);
    }

    @Override
    public void orderGroups(CommandSender commandSender, List<String> order) {
        groups.set("Groups", null);
        for (String set : order) {
            GroupData groupData = handler.getGroupData(set);
            if (groupData != null) {
                storeGroup(groupData);
            }
        }

        for (GroupData groupData : handler.getGroupData()) {
            if (!groups.contains("Groups." + groupData.getGroupName())) {
                storeGroup(groupData);
            }
        }

        save(groups, groupsFile);
    }

    private void save(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerTag(Player player) {
        PlayerData data = PlayerData.fromFile(player.getUniqueId().toString(), players);
        if (data != null) {
            data.setName(player.getName());
            handler.storePlayerData(player.getUniqueId(), data);
        }
    }

    private void loadPlayers() {
        for (Player player : Utils.getOnline()) {
            loadPlayerTag(player);
        }
    }

    private void loadGroups() {
        List<GroupData> groupData = new ArrayList<>();

        for (String groupName : groups.getConfigurationSection("Groups").getKeys(false)) {
            GroupData data = new GroupData();
            data.setGroupName(groupName);
            data.setPermission(groups.getString("Groups." + groupName + ".Permission", "nte.default"));
            data.setPrefix(groups.getString("Groups." + groupName + ".Prefix", ""));
            data.setSuffix(groups.getString("Groups." + groupName + ".Suffix", ""));
            data.setSortPriority(groups.getInt("Groups." + groupName + ".SortPriority", -1));
            groupData.add(data);
        }

        handler.assignGroupData(groupData);
    }

    private void storeGroup(GroupData groupData) {
        groups.set("Groups." + groupData.getGroupName() + ".Permission", groupData.getPermission());
        groups.set("Groups." + groupData.getGroupName() + ".Prefix", Utils.deformat(groupData.getPrefix()));
        groups.set("Groups." + groupData.getGroupName() + ".Suffix", Utils.deformat(groupData.getSuffix()));
        groups.set("Groups." + groupData.getGroupName() + ".SortPriority", groupData.getSortPriority());
    }

}