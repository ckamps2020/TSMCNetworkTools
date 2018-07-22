package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.Sounds;
import com.thesquadmc.networktools.commands.StaffmodeCommand;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.GameMsgs;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.ConnectionUtils;
import com.thesquadmc.networktools.utils.server.Multithreading;
import com.thesquadmc.networktools.utils.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerManagementChannel implements RedisChannel {

    private final NetworkTools plugin;

    public ServerManagementChannel(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equalsIgnoreCase(RedisChannels.STOP.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            String msg = object.get(RedisArg.MESSAGE.getName()).getAsString();

            if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
                Bukkit.broadcastMessage(CC.translate("&7"));
                Bukkit.broadcastMessage(CC.translate("&7"));
                Bukkit.broadcastMessage(CC.translate("&e&lSTOP &6■ &7Server restarting in &e15 &7seconds for reason: &e" + msg));
                Bukkit.broadcastMessage(CC.translate("&7"));
                Bukkit.broadcastMessage(CC.translate("&7"));
                Bukkit.getScheduler().runTask(plugin, () -> {
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
                                p.performCommand("warp");
                                p.sendMessage(CC.translate("&e&lSTOP &6■ &7Due to server restart you have been taken out of staffmode"));
                            }

                            if (user.getSetting(PlayerSetting.VANISHED) || user.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                                PlayerUtils.showPlayerSpectator(p);
                                user.updateSetting(PlayerSetting.VANISHED, false);
                                user.updateSetting(PlayerSetting.YOUTUBE_VANISHED, false);
                                p.sendMessage(CC.translate("&e&lSTOP &6■ &7Due to server restart you have been taken out of vanish"));
                            }
                        }
                        p.playSound(p.getLocation(), Sounds.ANVIL_USE.bukkitSound(), 1.0f, 1.0f);
                    }
                });
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(CC.translate("&e&lSTOP &6■ &7Server restarting for reason: &e" + msg));
                    }

                    Bukkit.shutdown();
                    Bukkit.broadcastMessage(CC.translate("&e&lSTOP &6■ &7Server restarting for reason: &e" + msg));
                }, 10 * 20);
            }

        } else if (channel.equalsIgnoreCase(RedisChannels.RETURN_SERVER.getName())) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Multithreading.runAsync(() -> {
                String server = object.get(RedisArg.ORIGIN_SERVER.getName()).getAsString();
                if (Bukkit.getServerName().equalsIgnoreCase(server)) {
                    String player = object.get(RedisArg.ORIGIN_PLAYER.getName()).getAsString();
                    String newServer = object.get(RedisArg.SERVER.getName()).getAsString();
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
            }));
        } else if (channel.equalsIgnoreCase(RedisChannels.STARTUP_REQUEST.getName())) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Multithreading.runAsync(() -> ServerUtils.updateServerState(plugin.getServerState())));

        } else if (channel.equalsIgnoreCase(RedisChannels.PLAYER_COUNT.getName())) {
            try { //TODO Remove
                String server = object.get(RedisArg.SERVER.getName()).getAsString();
                int count = object.get(RedisArg.COUNT.getName()).getAsInt();

                plugin.getCountManager().getCount().put(server, count);
            } catch (NullPointerException ignored) {
            }
        }
    }
}
