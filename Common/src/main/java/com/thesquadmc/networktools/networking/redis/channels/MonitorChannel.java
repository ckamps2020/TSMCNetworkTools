package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.Multithreading;
import com.thesquadmc.networktools.utils.server.ServerUtils;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MonitorChannel implements RedisChannel {

    private final NetworkTools plugin;

    public MonitorChannel(NetworkTools plugin) {
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
                        if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN) && TSMCUser.fromPlayer(player).getSetting(PlayerSetting.MONITOR)) {
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
                        if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN) && TSMCUser.fromPlayer(player).getSetting(PlayerSetting.MONITOR)) {
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
