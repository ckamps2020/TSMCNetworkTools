package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class MonitorChannel implements RedisChannel {

    private final Main plugin;

    public MonitorChannel(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.MONITOR_REQUEST.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            if (server.equalsIgnoreCase(Bukkit.getServerName())) {
                plugin.getRedisManager().sendMessage(RedisChannels.MONITOR_RETURN, RedisMesage.newMessage()
                        .set(RedisArg.SERVER.getName(), server)
                        .set(RedisArg.UPTIME.getName(), TimeUtils.getFormattedTime(System.currentTimeMillis() - plugin.getStartup()))
                        .set(RedisArg.COUNT.getName(), Bukkit.getOnlinePlayers().size())
                        .set(RedisArg.TPS.getName(), ServerUtils.getTPS(0))
                        .set(RedisArg.MESSAGE.getName(), String.format("&7TPS = &e%s&7, Memory = &e%s&8/&e%s", ServerUtils.getTPS(0), ServerUtils.getUsedMemory(), ServerUtils.getTotalMemory())));

                /*Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Multithreading.runAsync(() -> {
                    try (Jedis jedis = Main.getMain().getPool().getResource()) {
                        JedisTask.withName(UUID.randomUUID().toString())
                                .withArg(RedisArg.SERVER.getName(), server)
                                .withArg(RedisArg.UPTIME.getName(), TimeUtils.millisToRoundedTime(System.currentTimeMillis() - plugin.getStartup()))
                                .withArg(RedisArg.COUNT.getName(), String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .withArg(RedisArg.TPS.getName(), ServerUtils.getTPS(0))
                                .withArg(RedisArg.MESSAGE.getName(), "&7TPS = &e" + ServerUtils.getTPS(0) + "&7, &7Memory = &e" + ServerUtils.getUsedMemory() + "&8/&e" + ServerUtils.getTotalMemory() + "&7")
                                .send(RedisChannels.MONITOR_RETURN.getName(), jedis);
                    }
                }));*/
            }
        } else if (channel.equalsIgnoreCase(RedisChannels.MONITOR_INFO.getName())) {
            Multithreading.runAsync(() -> {
                String server = object.get(RedisArg.SERVER.getName()).getAsString();
                String count = object.get(RedisArg.COUNT.getName()).getAsString();
                String msg = object.get(RedisArg.MESSAGE.getName()).getAsString();
                String uptime = object.get(RedisArg.UPTIME.getName()).getAsString();
                String tps = object.get(RedisArg.TPS.getName()).getAsString();
                if (!uptime.equalsIgnoreCase("0")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN) && TSMCUser.fromPlayer(player).hasMonitor()) {
                            if (!tps.equalsIgnoreCase("null") && Double.valueOf(tps) > 15.0) {
                                player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
                                player.sendMessage(CC.translate("&6&l[MONITOR REPORT] &f" + server + " &7" + count + "&8/&7200"));
                                player.sendMessage(CC.translate("&7"));
                                player.sendMessage(CC.translate(msg));
                                player.sendMessage(CC.translate("&7Uptime = &e" + uptime));
                                player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
                            } else {
                                player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
                                player.sendMessage(CC.translate("&6&l[MONITOR REPORT] &f" + server + " &cBelow 15 TPS!"));
                                player.sendMessage(CC.translate("&7"));
                                player.sendMessage(CC.translate(msg));
                                player.sendMessage(CC.translate("&7Uptime = &e" + uptime));
                                player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
                            }
                        }
                    }
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN) && TSMCUser.fromPlayer(player).hasMonitor()) {
                            player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
                            player.sendMessage(CC.translate("&6&l[MONITOR REPORT] &f" + server + " &7" + count + "&8/&7200"));
                            player.sendMessage(CC.translate("&7"));
                            player.sendMessage(CC.translate(msg));
                            player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
                        }
                    }
                }
            });
        }
    }
}
