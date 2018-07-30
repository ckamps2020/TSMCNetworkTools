package com.thesquadmc.networktools.listeners;

import com.google.common.collect.Maps;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.MojangGameProfile;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public final class ConnectionListeners implements Listener {

    private final NetworkTools plugin;
    private final File dataFolder;

    private final Map<UUID, Long> loginTimes = Maps.newHashMap();

    public ConnectionListeners(NetworkTools plugin) {
        this.plugin = plugin;

        dataFolder = new File(plugin.getDataFolder(), "userdata");
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            plugin.getLogger().info("No `userdata` folder present, creating one...");
            dataFolder.mkdirs();
        }
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent e) {
        plugin.getUserDatabase().getUser(e.getUniqueId()).thenApply(user -> {
            if (user == null) {
                e.setKickMessage(CC.RED + "Cannot load your account! \nPlease contact a Staff!");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);

                return false;
            }

            user.addIP(e.getAddress().getHostAddress());

            TSMCUser.loadUser(user);
            if (!user.getName().equalsIgnoreCase(e.getName())) {
                user.addPreviousName(user.getName());
                user.setName(e.getName());
            }

            return true;
        });

        LocalPlayer player = null;
        try {
            File loadFile = new File(dataFolder, e.getUniqueId().toString() + ".json");
            if (loadFile.exists()) {
                player = JSONUtils.getGson().fromJson(
                        new FileReader(loadFile),
                        LocalPlayer.class
                );
            }
        } catch (IOException ignored) {
        }

        if (player == null) {
            player = new LocalPlayer(e.getUniqueId(), e.getName());
        }

        //Please run any checks before this block of code
        //to prevent the player object from being added
        //to the map
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        plugin.getLocalPlayerManager().addPlayer(e.getUniqueId(), player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        Multithreading.runAsync(() -> {
            if (plugin.getMcLeaksAPI().checkAccount(e.getPlayer().getUniqueId()).isMCLeaks()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!e.getPlayer().isOnline()) { //Incase they log off before response comes
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), e.getPlayer().getName() + " is an MCLeaks account!");
                        return;
                    }

                    e.getPlayer().kickPlayer(CC.RED + "You are using a Compromised Account\n If this is wrong, please contact Staff!");
                });
            }
        });

        loginTimes.put(player.getUniqueId(), System.currentTimeMillis());

        TSMCUser user = TSMCUser.fromPlayer(player);
        if (user.isNicknamed()) {
            PlayerUtils.setName(player, user.getNickname());
        }

        MojangGameProfile profile = plugin.getNMSAbstract().getGameProfile(player);
        profile.getPropertyMap().values().forEach(p -> {
            user.setSkinKey(p.getValue());
            user.setSignature(p.getSignature());
        });

        PlayerUtils.unfreezePlayer(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Bukkit.getServerName().toUpperCase().startsWith("MG")
                    || Bukkit.getServerName().toUpperCase().startsWith("FACTIONS")
                    || Bukkit.getServerName().toUpperCase().startsWith("HUB")
                    || Bukkit.getServerName().toUpperCase().startsWith("CREATIVE")) {
                if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
                    player.chat("/ev");
                }
            }

            if (Bukkit.getServerName().toUpperCase().startsWith("MG")
                    || Bukkit.getServerName().toUpperCase().startsWith("FACTIONS")
                    || Bukkit.getServerName().toUpperCase().startsWith("HUB")
                    || Bukkit.getServerName().toUpperCase().startsWith("PRISON")
                    || Bukkit.getServerName().toUpperCase().startsWith("SKYBLOCK")
                    || Bukkit.getServerName().toUpperCase().startsWith("CREATIVE")) {
                if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
                    player.chat("/vanish");
                }
            }

            if (!NetworkTools.getInstance().getSig().equalsIgnoreCase("NONE")) {
                PlayerUtils.setSameSkin(player);
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                TSMCUser targetUser = TSMCUser.fromPlayer(p);
                if (targetUser.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                    PlayerUtils.hidePlayerSpectatorYT(p);
                } else if (targetUser.getSetting(PlayerSetting.VANISHED)) {
                    PlayerUtils.hidePlayerSpectatorStaff(p);
                }
            }
        }, 3L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        player.performCommand("party leave");

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (TSMCUser.fromPlayer(p).getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                p.showPlayer(player);
            }
        }

        TSMCUser user = TSMCUser.fromPlayer(player);

        long time = System.currentTimeMillis() - loginTimes.remove(player.getUniqueId());
        user.addTimePlayed(plugin.getServerType(), time);

        TSMCUser.unloadUser(user, true);

        LocalPlayer localPlayer = plugin.getLocalPlayerManager().removePlayer(player);
        Multithreading.runAsync(() -> {
            try (FileWriter writer = new FileWriter(new File(dataFolder, player.getUniqueId() + ".json"))) {
                JSONUtils.getGson().toJson(
                        localPlayer,
                        writer
                );

            } catch (IOException exception) {
                exception.printStackTrace();

                plugin.getLogger().severe("Could not save data of user " + player.getName());
            }
        });
    }

}
