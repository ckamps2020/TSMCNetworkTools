package com.thesquadmc.networktools.listeners;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.MojangGameProfile;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.converter.EssentialsPlayerConverter;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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

            new EssentialsPlayerConverter(plugin, player);
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
    public void on(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        Multithreading.runAsync(() -> {
            if (plugin.getMcLeaksAPI().checkAccount(e.getPlayer().getUniqueId()).isMCLeaks()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!e.getPlayer().isOnline()) { //Incase they log off before response comes
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sc " + e.getPlayer().getName() + " is an MCLeaks account!");
                        return;
                    }

                    e.getPlayer().kickPlayer(CC.RED + "You are using a Compromised Account\n If this is wrong, please contact Staff!");
                });
            }
        });

        TSMCUser user = TSMCUser.fromPlayer(player);
        if (user.getFirstJoin() == null) {
            user.setFirstJoin(new Date());
        }

        user.setLastJoin(new Date());

        if (user.isNicknamed()) {
            PlayerUtils.setName(player, user.getNickname());
        }

        //Staff checks
        if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
            if (user.getSetting(PlayerSetting.AUTO_VANISH)) user.updateSetting(PlayerSetting.AUTO_VANISH, false);
            if (user.getSetting(PlayerSetting.VANISHED)) user.updateSetting(PlayerSetting.VANISHED, false);
            if (user.getSetting(PlayerSetting.YOUTUBE_VANISHED))
                user.updateSetting(PlayerSetting.YOUTUBE_VANISHED, false);
            if (user.getSetting(PlayerSetting.SOCIALSPY)) user.updateSetting(PlayerSetting.SOCIALSPY, false);
            if (user.getSetting(PlayerSetting.FORCEFIELD)) user.updateSetting(PlayerSetting.FORCEFIELD, false);

        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                plugin.getRedisManager().executeJedisAsync(jedis -> {
                    JsonObject object = new JsonObject();
                    object.add("server", JSONUtils.getGson().toJsonTree(Bukkit.getServerName()));
                    object.add("rank", JSONUtils.getGson().toJsonTree(PlayerUtils.getStaffRank(player).name()));
                    object.add("vanished", JSONUtils.getGson().toJsonTree(user.getSetting(PlayerSetting.VANISHED) || user.getSetting(PlayerSetting.YOUTUBE_VANISHED)));

                    jedis.hset("staff", user.getName(), object.toString());
                });
            }, 2 * 20); //Move this to BungeeCord
        }

        MojangGameProfile profile = plugin.getNMSAbstract().getGameProfile(player);
        profile.getPropertyMap().values().forEach(p -> {
            user.setSkinKey(p.getValue());
            user.setSignature(p.getSignature());
        });

        PlayerUtils.unfreezePlayer(player);
        if (!plugin.getSig().equalsIgnoreCase("NONE")) {
            PlayerUtils.setSameSkin(player);
        }

        if (user.getSetting(PlayerSetting.AUTO_VANISH)) {
            player.chat("/ev off");
            player.chat("/vanish on");
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            TSMCUser target = TSMCUser.fromPlayer(p);
            if (target.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                PlayerUtils.hidePlayerSpectatorYT(p);

            } else if (target.getSetting(PlayerSetting.VANISHED)) {
                PlayerUtils.hidePlayerSpectatorStaff(p);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (TSMCUser.fromPlayer(p).getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                p.showPlayer(player);
            }
        }

        TSMCUser user = TSMCUser.fromPlayer(player);

        String name = user.getName();
        plugin.getRedisManager().executeJedisAsync(jedis -> {
            if (jedis.hexists("staff", name)) {
                jedis.hdel("staff", name);
            }
        });

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
