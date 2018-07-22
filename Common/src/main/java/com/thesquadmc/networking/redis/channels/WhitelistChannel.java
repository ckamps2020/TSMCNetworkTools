package com.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.NetworkTools;
import com.thesquadmc.networking.redis.RedisChannel;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class WhitelistChannel implements RedisChannel {

    private final NetworkTools plugin;

    public WhitelistChannel(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.WHITELIST_ADD.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
                String name = object.get(RedisArg.PLAYER.getName()).getAsString();

                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Bukkit.dispatchCommand(console, "minecraft:whitelist add " + name);
            }

        } else if (channel.equals(RedisChannels.WHITELIST_REMOVE.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
                String name = object.get(RedisArg.PLAYER.getName()).getAsString();

                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Bukkit.dispatchCommand(console, "minecraft:whitelist remove " + name);
            }

        } else if (channel.equals(RedisChannels.WHITELIST.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
                String onoff = object.get(RedisArg.ONOFF.getName()).getAsString();
                String msg = object.get(RedisArg.MESSAGE.getName()).getAsString();
                if (onoff.equalsIgnoreCase("ON")) {
                    Bukkit.broadcastMessage(CC.translate("&e&lWHITELIST &6■ &7Whitelist has been enabled for reason: &e" + msg));

                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, "minecraft:whitelist on");

                    plugin.setWhitelistMessage(CC.translate(msg));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getName());
                        if (!PlayerUtils.isEqualOrHigherThen(p, Rank.MANAGER) && !Bukkit.getWhitelistedPlayers().contains(offlinePlayer)) {
                            p.kickPlayer(CC.translate("&7Whitelist enabled \n&e" + msg));
                        }
                    }
                } else {
                    Bukkit.broadcastMessage(CC.translate("&e&lWHITELIST &6■ &7Whitelist has been disabled for reason: &e" + msg));

                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, "minecraft:whitelist off");
                }
            }
        }
    }
}
