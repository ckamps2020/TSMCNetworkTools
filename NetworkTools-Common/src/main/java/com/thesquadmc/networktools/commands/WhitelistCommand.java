package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WhitelistCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public WhitelistCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
                            stringBuilder.append(offlinePlayer.getName()).append(" ");
                        }
                        player.sendMessage(CC.translate("&e&lWhitelisted users &7(" + Bukkit.getWhitelistedPlayers().size() + "): &f" + stringBuilder.toString()));
                    } else {
                        player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist <servertype|server|all> <on|off> <reason>"));
                        player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist add <player> <servertype|server|all>"));
                        player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist remove <player> <servertype|server|all>"));
                        player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist list"));
                    }
                } else if (args.length >= 3) {
                    String a = args[0];
                    if (a.equalsIgnoreCase("add")) {
                        String name = args[1];
                        String server = args[2];
                        player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have added &e" + name + " &7to the whitelist on server &e" + server));

                        networkTools.getRedisManager().sendMessage(RedisChannels.WHITELIST_ADD, RedisMesage.newMessage()
                                .set(RedisArg.SERVER, server.toUpperCase())
                                .set(RedisArg.PLAYER, name));
                    } else if (a.equalsIgnoreCase("remove")) {
                        String name = args[1];
                        String server = args[2];
                        player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have removed &e" + name + " &7to the whitelist on server &e" + server));

                        networkTools.getRedisManager().sendMessage(RedisChannels.WHITELIST_REMOVE, RedisMesage.newMessage()
                                .set(RedisArg.SERVER, server.toUpperCase())
                                .set(RedisArg.PLAYER, name));
                    } else {
                        String server = args[0];
                        String onoff = args[1];
                        if (onoff.equalsIgnoreCase("ON")) {
                            if (server.equalsIgnoreCase("ALL")) {
                                player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have whitelisted &eall servers&7"));
                            } else {
                                player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have whitelisted &e" + server + "&7"));
                            }
                        } else if (onoff.equalsIgnoreCase("OFF")) {
                            if (server.equalsIgnoreCase("ALL")) {
                                player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have turned whitelist off on &eall servers&7"));
                            } else {
                                player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have turned whitelist off on &e" + server + "&7"));
                            }
                        } else {
                            player.sendMessage(CC.translate("&e&lWHITELIST &6■ &e" + onoff + " &7is not valid and should either be on or off!"));
                            return true;
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            stringBuilder.append(args[i]).append(" ");
                        }

                        networkTools.getRedisManager().sendMessage(RedisChannels.WHITELIST, RedisMesage.newMessage()
                                .set(RedisArg.SERVER, server.toUpperCase())
                                .set(RedisArg.ONOFF, onoff)
                                .set(RedisArg.MESSAGE, stringBuilder.toString()));
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist <servertype|server|all> <on|off> <reason>"));
                    player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist add <player> <servertype|server|all>"));
                    player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist remove <player> <servertype|server|all>"));
                    player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7Usage: /whitelist list"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
