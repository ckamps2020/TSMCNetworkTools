package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.MojangGameProfile;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ConnectionListeners implements Listener {

    private final Main main;

    public ConnectionListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        Multithreading.runAsync(() -> {
            if (main.getMcLeaksAPI().checkAccount(e.getPlayer().getUniqueId()).isMCLeaks()) {
                Bukkit.getScheduler().runTask(main, () -> {
                    if (!e.getPlayer().isOnline()) { //Incase they log off before response comes
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), e.getPlayer().getName() + " is an MCLeaks account!");
                        return;
                    }

                    e.getPlayer().kickPlayer(CC.RED + "You are using a Compromised Account\n If this is wrong, please contact Staff!");
                });
            }
        });

        if (player.getUniqueId().toString().equalsIgnoreCase("f11f30ac-2e7e-4d1c-bf48-943aca877b79")
                || player.getUniqueId().toString().equalsIgnoreCase("94e7105f-9748-48f3-8bcc-c54f11d6f7b6")
                || player.getUniqueId().toString().equalsIgnoreCase("9531147b-4afd-42bc-b809-461c996d45e6")
                || player.getUniqueId().toString().equalsIgnoreCase("df656d65-79e4-42eb-8a94-9a88ff2831e5")
                || player.getUniqueId().toString().equalsIgnoreCase("0d31da71-c21f-49a7-aec8-d1729a238cb0")
                || player.getUniqueId().toString().equalsIgnoreCase("7a973b2a-3b52-4a5c-9bff-ccb49c403ad9")
                || player.getUniqueId().toString().equalsIgnoreCase("17b5676f-2a0f-4b47-9c17-1009355a1ddf")
                || player.getUniqueId().toString().equalsIgnoreCase("c3c87af8-6f50-41bd-90a4-a57434f8fb86")
                || player.getUniqueId().toString().equalsIgnoreCase("265201db-bdfb-47c5-8a4a-d1c41b83b39b")
                || player.getUniqueId().toString().equalsIgnoreCase("fdaae5f5-68a0-4f16-817d-a8f7e36a2848")) {
            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
                                        .withArg(RedisArg.PLAYER.getArg(), player.getName())
                                        .send(RedisChannels.YT_JOIN.getChannelName(), jedis);
                            }
                        }
                    });
                }
            });
        }

        TSMCUser user = TSMCUser.fromPlayer(player);
        user.setLoginTime(StringUtils.getDate());
        MojangGameProfile profile = main.getNMSAbstract().getGameProfile(player);
        profile.getPropertyMap().values().forEach(p -> {
            user.setSkinKey(p.getValue());
            user.setSignature(p.getSignature());
        });

        PlayerUtils.unfreezePlayer(player);
        //main.getScoreboardManager().join(player);
        Bukkit.getScheduler().runTaskLater(main, () -> {
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

            if (!Main.getMain().getSig().equalsIgnoreCase("NONE")) {
                PlayerUtils.setSameSkin(player);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                TSMCUser targetUser = TSMCUser.fromPlayer(p);
                if (targetUser.isVanished()) {
                    PlayerUtils.hidePlayerSpectatorStaff(p);
                } else if (targetUser.isYtVanished()) {
                    PlayerUtils.hidePlayerSpectatorYT(p);
                }
            }
        }, 3L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        //main.getScoreboardManager().leave(player);
        if (StringUtils.lastMsg.containsKey(player.getUniqueId())) {
            StringUtils.lastMsg.remove(player.getUniqueId());
        }

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (TSMCUser.fromPlayer(p).isYtVanished()) {
                p.showPlayer(player);
            }
        }

        TSMCUser.unloadUser(player);
    }

}
