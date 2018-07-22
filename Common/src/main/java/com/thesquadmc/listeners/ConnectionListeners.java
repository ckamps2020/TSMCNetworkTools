package com.thesquadmc.listeners;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.abstraction.MojangGameProfile;
import com.thesquadmc.player.PlayerSetting;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.player.local.LocalPlayer;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.json.JSONUtils;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.msgs.StringUtils;
import com.thesquadmc.utils.player.PlayerUtils;
import com.thesquadmc.utils.server.Multithreading;
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

public final class ConnectionListeners implements Listener {

    private final NetworkTools plugin;
    private final File dataFolder;

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

        TSMCUser user = TSMCUser.fromPlayer(player);
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
        StringUtils.lastMsg.remove(player.getUniqueId());

        player.performCommand("party leave");

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (TSMCUser.fromPlayer(p).getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                p.showPlayer(player);
            }
        }

        TSMCUser.unloadUser(TSMCUser.fromPlayer(player), true);

        LocalPlayer localPlayer = plugin.getLocalPlayerManager().removePlayer(player);
        try (FileWriter writer = new FileWriter(new File(dataFolder, player.getUniqueId() + ".json"))) {
            JSONUtils.getGson().toJson(
                    localPlayer,
                    writer
            );

        } catch (IOException exception) {
            exception.printStackTrace();

            plugin.getLogger().severe("Could not save data of user " + player.getName());
        }
    }

}
