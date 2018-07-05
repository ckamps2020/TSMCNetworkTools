package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.abstraction.Sounds;
import me.thesquadmc.commands.StaffmodeCommand;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.GameMsgs;
import me.thesquadmc.utils.player.PlayerUtils;
import me.thesquadmc.utils.server.ConnectionUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerManagementChannel implements RedisChannel {

    private final Main plugin;

    public ServerManagementChannel(Main plugin) {
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
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            int count = object.get(RedisArg.COUNT.getName()).getAsInt();

            plugin.getCountManager().getCount().put(server, count);
        }
    }
}
