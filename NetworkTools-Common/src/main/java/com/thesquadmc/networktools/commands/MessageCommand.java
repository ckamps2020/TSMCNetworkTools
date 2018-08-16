package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.chat.event.PlayerMessageEvent;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class MessageCommand {

    private final NetworkTools plugin;

    public MessageCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"message", "msg"}, playerOnly = true)
    public void messgae(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() < 2) {
            player.sendMessage(CC.RED + "/message <player> <message>");
            return;
        }

        String name = args.getArg(0);
        String message = String.join(" ", Arrays.copyOfRange(args.getArgs(), 1, args.length()));

        Player target = Bukkit.getPlayer(name);
        if (target != null) {
            if (player.getName().equals(target.getName())) {
                player.sendMessage(CC.RED + "You cannot message yourself");
                return;
            }

            if (TSMCUser.fromPlayer(target).isIgnored(player.getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot send messages to this player!");
                return;
            }

            player.sendMessage(CC.translate("&6Me &7■ &6{0} &8» &e{1}", target.getName(), message));
            target.sendMessage(CC.translate("&6{0} &7■ &6Me &8» &e{1}", player.getName(), message));

            Bukkit.getPluginManager().callEvent(new PlayerMessageEvent(player.getName(), target.getName(), message));

            TSMCUser.fromPlayer(player).setLastMessager(target.getUniqueId());
            TSMCUser.fromPlayer(target).setLastMessager(player.getUniqueId());

        } else {
            plugin.getUUIDTranslator().getUUID(name, false).thenAccept(user -> {
                if (user == null) {
                    player.sendMessage(CC.RED + name + " could not be found");
                    return;
                }

                PlayerUtils.isOnline(user).thenAccept(online -> {
                    if (online) {
                        TSMCUser p = TSMCUser.fromPlayer(player);

                        plugin.getRedisManager().sendMessage(RedisChannels.MESSAGE, RedisMesage.newMessage()
                                .set("sender", player.getUniqueId())
                                .set("sender_name", p.getName())
                                .set("target", user)
                                .set("message", message)
                                .set("bypass", PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)));

                    } else {
                        player.sendMessage(CC.RED + "Could not find " + name + " (redis)");
                    }
                });
            });
        }
    }

    @Command(name = {"togglemessage", "togglepm", "togglepms"}, playerOnly = true)
    public void toggle(CommandArgs args) {
        TSMCUser user = TSMCUser.fromPlayer(args.getPlayer());

        boolean change = !user.getSetting(PlayerSetting.PRIVATE_MESSAGES);
        user.updateSetting(PlayerSetting.PRIVATE_MESSAGES, change);
        args.getSender().sendMessage(CC.translate("&e&lMESSAGES &6■ &7You have turned {0} &7private messages!", (change ? "&aon" : "&coff")));
    }

    @Command(name = {"reply", "r"}, playerOnly = true)
    public void reply(CommandArgs args) {
        Player player = args.getPlayer();

        TSMCUser user = TSMCUser.fromPlayer(player);
        UUID uuid = user.getLastMessager();

        if (uuid == null) {
            player.sendMessage(CC.RED + "You have no one to reply to!");
            return;
        }

        PlayerUtils.isOnline(uuid).whenComplete((online, throwable) -> {
            if (online) {
                String message = String.join(" ", Arrays.copyOfRange(args.getArgs(), 0, args.length()));

                plugin.getRedisManager().sendMessage(RedisChannels.MESSAGE, RedisMesage.newMessage()
                        .set("sender", player.getUniqueId())
                        .set("sender_name", player.getName())
                        .set("target", uuid)
                        .set("message", message)
                        .set("bypass", PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)));
            } else {
                player.sendMessage(CC.RED + "Could not find the last player you messaged!");
            }
        });
    }

    @Command(name = {"socialspy"}, permission = "essentials.socialspy", playerOnly = true)
    public void socialspy(CommandArgs args) {
        Player player = args.getPlayer();
        TSMCUser user = TSMCUser.fromPlayer(player);

        boolean socialspy = !user.getSetting(PlayerSetting.SOCIALSPY);
        user.updateSetting(PlayerSetting.SOCIALSPY, socialspy);
        args.getSender().sendMessage(CC.translate("&e&lMESSAGES &6■ &7You have turned {0} &7SocialSpy!", (socialspy ? "&aon" : "&coff")));

    }
}
