package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.abstraction.Sounds;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.GameMsgs;
import me.thesquadmc.utils.server.ConnectionUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerManagementChannel implements RedisChannel {

    private final Main plugin;

    public ServerManagementChannel(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
    }if (channel.equalsIgnoreCase(RedisChannels.STOP.getChannelName())) {
        String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
        if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
            String msg = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.broadcastMessage(CC.translate("&e&lSTOP &6■ &7Server restarting in &e15 &7seconds for reason: &e" + msg));
            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.getScheduler().runTask(main, new Runnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (PlayerUtils.isEqualOrHigherThen(p, Rank.MOD)) {
                            TSMCUser user = TSMCUser.fromPlayer(p);
                            if (StaffmodeCommand.getStaffmode().containsKey(p.getUniqueId())) {
                                p.getInventory().clear();
                                for (ItemStack itemStack : StaffmodeCommand.getStaffmode().get(p.getUniqueId())) {
                                    if (itemStack != null) {
                                        p.getInventory().addItem(itemStack);
                                    }
                                }
                                StaffmodeCommand.getStaffmode().remove(p.getUniqueId());
                                p.setGameMode(GameMode.SURVIVAL);
                                StaffmodeCommand.getStaffmode().remove(p.getUniqueId());
                                p.performCommand("spawn");
                                p.sendMessage(CC.translate("&e&lSTOP &6■ &7Due to server restart you have been taken out of staffmode"));
                            }
                            if (user.isVanished() || user.isYtVanished()) {
                                PlayerUtils.showPlayerSpectator(p);
                                user.setVanished(false);
                                user.setYtVanished(false);
                                p.sendMessage(CC.translate("&e&lSTOP &6■ &7Due to server restart you have been taken out of vanish"));
                            }
                        }
                        p.playSound(p.getLocation(), Sounds.ANVIL_USE.bukkitSound(), 1.0f, 1.0f);
                    }
                }
            });
            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(CC.translate("&e&lSTOP &6■ &7Server restarting for reason: &e" + msg));
                    }
                }
            }, 10 * 20);
            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {
                    Bukkit.shutdown();
                    Bukkit.broadcastMessage(CC.translate("&e&lSTOP &6■ &7Server restarting for reason: &e" + msg));
                }
            }, 15 * 20L);
        }
    } else if (channel.equalsIgnoreCase(RedisChannels.MONITOR_REQUEST.getChannelName())) {
        String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
        if (server.equalsIgnoreCase(Bukkit.getServerName())) {
            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.SERVER.getArg(), server)
                                        .withArg(RedisArg.UPTIME.getArg(), TimeUtils.millisToRoundedTime(System.currentTimeMillis() - main.getStartup()))
                                        .withArg(RedisArg.COUNT.getArg(), String.valueOf(Bukkit.getOnlinePlayers().size()))
                                        .withArg(RedisArg.TPS.getArg(), ServerUtils.getTPS(0))
                                        .withArg(RedisArg.MESSAGE.getArg(), "&7TPS = &e" + ServerUtils.getTPS(0) + "&7, &7Memory = &e" + ServerUtils.getUsedMemory() + "&8/&e" + ServerUtils.getTotalMemory() + "&7")
                                        .send(RedisChannels.MONITOR_RETURN.getChannelName(), jedis);
                            }
                        }
                    });
                }
            });
        }
    } else if (channel.equalsIgnoreCase(RedisChannels.MONITOR_INFO.getChannelName())) {
        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
                String count = String.valueOf(data.get(RedisArg.COUNT.getArg()));
                String msg = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
                String uptime = String.valueOf(data.get(RedisArg.UPTIME.getArg()));
                String tps = String.valueOf(data.get(RedisArg.TPS.getArg()));
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
            }
        });
    } else if (channel.equalsIgnoreCase(RedisChannels.RETURN_SERVER.getChannelName())) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        String server = String.valueOf(data.get(RedisArg.ORIGIN_SERVER.getArg()));
                        if (Bukkit.getServerName().equalsIgnoreCase(server)) {
                            String player = String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg()));
                            String newServer = String.valueOf(data.get(RedisArg.SERVER.getArg()));
                            if (newServer.equalsIgnoreCase("NONE")) {
                                if (Bukkit.getPlayer(player) != null) {
                                    Player p = Bukkit.getPlayer(player);
                                    ConnectionUtils.getFetching().remove(p.getUniqueId());
                                    p.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Unable to find an open server right now! Please try again"));
                                }
                            } else {
                                if (Bukkit.getPlayer(player) != null) {
                                    Player p = Bukkit.getPlayer(player);
                                    ConnectionUtils.getFetching().remove(p.getUniqueId());
                                    ConnectionUtils.sendPlayer(p, newServer, true);
                                }
                            }
                        }
                    }
                });
            }
        });
    } else if (channel.equalsIgnoreCase(RedisChannels.STARTUP_REQUEST.getChannelName())) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        ServerUtils.updateServerState(main.getServerState());
                    }
                });
            }
        });
    } else if (channel.equalsIgnoreCase(RedisChannels.PLAYER_COUNT.getChannelName())) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
                        Double count = Double.valueOf(String.valueOf(data.get(RedisArg.COUNT.getArg())));
                        main.getCountManager().getCount().put(server, count.intValue());
                    }
                });
            }
        });
    }
}
