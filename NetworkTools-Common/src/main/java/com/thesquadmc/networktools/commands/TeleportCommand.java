package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.message.ClickableMessageBuilder;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.Unicode;
import com.thesquadmc.networktools.utils.player.TimedTeleport;
import com.thesquadmc.networktools.utils.server.ServerType;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Stream;

public class TeleportCommand {

    private static final int DEFAUFT = Integer.MIN_VALUE;

    private final NetworkTools plugin;

    public TeleportCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"tptoggle", "etptoggle"})
    public void tptoggle(CommandArgs args) {
        TSMCUser user = TSMCUser.fromPlayer(args.getPlayer());

        boolean change = !user.getSetting(PlayerSetting.TELEPORT_REQUESTS);
        user.updateSetting(PlayerSetting.TELEPORT_REQUESTS, change);
        args.getSender().sendMessage(CC.translate("&e&lTELEPORT &6■ &7You have turned {0} &7teleport requests!", (change ? "&aon" : "&coff")));
    }

    @Command(name = {"teleport", "tp", "tpo"}, permission = "essentials.tp")
    public void teleport(CommandArgs args) {
        CommandSender sender = args.getSender();

        switch (args.length()) {
            case 1: { //TODO Clean this logic up
                if (sender instanceof Player) {
                    Player target = Bukkit.getPlayer(args.getArg(0));
                    if (target == null) {
                        sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                        return;
                    }

                    ((Player) sender).teleport(target);
                    sender.sendMessage(CC.B_YELLOW + "TELEPORT " + Unicode.SQUARE + CC.GRAY + " You teleported to " + target.getName());

                } else {
                    sender.sendMessage(CC.RED + "You must be a player to teleport yourself!");
                }

                break;
            }

            case 2: {
                if (sender.hasPermission("essentials.tp.others")) {
                    Player toTeleport = Bukkit.getPlayer(args.getArg(0));
                    if (toTeleport == null) {
                        sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                        return;
                    }

                    Player target = Bukkit.getPlayer(args.getArg(1));
                    if (target == null) {
                        sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                        return;
                    }

                    toTeleport.teleport(target);
                    sender.sendMessage(CC.B_YELLOW + "TELEPORT " + Unicode.SQUARE + CC.GRAY + " Teleported " + toTeleport.getName() + " to " + target.getName());

                } else {
                    sender.sendMessage(CC.RED + "You cannot teleport to other players!");
                }

                break;
            }

            case 3: {
                if (sender instanceof Player) {
                    int x = NumberUtils.toInt(args.getArg(0), DEFAUFT);
                    int y = NumberUtils.toInt(args.getArg(1), DEFAUFT);
                    int z = NumberUtils.toInt(args.getArg(2), DEFAUFT);

                    if (x == DEFAUFT || y == DEFAUFT || z == DEFAUFT) {
                        sender.sendMessage(CC.RED + "Not valid coordinates! /tp <x> <y> <z>");
                        return;
                    }

                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    ((Player) sender).teleport(location);

                } else {
                    sender.sendMessage(CC.RED + "Console cannot teleport to coordinates");
                }

                break;
            }

            default: {
                sender.sendMessage(CC.B_YELLOW + "TELEPORT " + Unicode.SQUARE + CC.GRAY + " Uh oh, something went wrong! /tp <player> [player]");
            }
        }
    }

    @Command(name = {"tphere", "tpohere"}, permission = "essentials.tphere", playerOnly = true)
    public void tphere(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You must specify a player to teleport!");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if (target == null) {
            player.sendMessage(CC.RED + "Could not find " + args.getArg(0));
            return;
        }

        target.teleport(player);

        target.sendMessage(CC.translate("&e&lTELEPORT &6■ &7You were teleported to &e{0}", player.getName()));
        player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7You teleported &e{0} &7to &eyou", target.getName()));
    }

    @Command(name = {"tpall"}, permission = "essentials.tpall")
    public void tpall(CommandArgs args) {
        Player player = args.getPlayer();

        new ClickableMessageBuilder(player)
                .message(CC.translate("&c&lARE YOU SURE YOU WANT TO TELEPORT ALL {0} PLAYERS TO YOU?", Bukkit.getOnlinePlayers().size() - 1))
                .onClick(p -> {
                    Bukkit.getOnlinePlayers().forEach(p1 -> {
                        p1.teleport(player);
                        p1.sendMessage(CC.translate("&e&lTELEPORT &6■ &7You were teleported to &e{0}", player.getName()));
                    });

                    player.sendMessage("&e&lTELEPORT &6■ &7You teleported all players to you");
                })
                .complete()
                .send();
    }

    @Command(name = {"tpa", "call", "ecall", "etpa", "tpask", "etpask"}, playerOnly = true)
    public void tpa(CommandArgs args) {
        Player player = args.getPlayer();

        if (plugin.getServerType() == ServerType.PRISON) {
            player.sendMessage(CC.RED + "You cannot teleport to players in Prison!");
            return;
        }

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/tpa <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if (target == null) {
            player.sendMessage(CC.RED + args.getArg(0) + " is not online!");
            return;
        }

        if (player == target) {
            player.sendMessage(CC.RED + "You can't teleport to yourself");
            return;
        }

        TSMCUser user = TSMCUser.fromPlayer(target);
        if (!user.getSetting(PlayerSetting.TELEPORT_REQUESTS)) {
            player.sendMessage(CC.translate("&c{0} has teleport requests disabled!", target.getName()));
            return;
        }

        if (user.isIgnored(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You cannot teleport to this player!");
            return;
        }

        LocalPlayer localTarget = plugin.getLocalPlayerManager().getPlayer(target);
        localTarget.requestTeleport(player, false);

        Stream.of(
                CC.translate("&e&lTELEPORT &6■ &e{0} &7has requested to teleport &eto you", player.getName()),
                CC.translate("&e&lTELEPORT &6■ &7To teleport, type &e/tpaccept"),
                CC.translate("&e&lTELEPORT &6■ &7To deny this request, type &e/tpdeny"),
                CC.translate("&e&lTELEPORT &6■ &7This request will timeout after &e60 seconds")
        ).forEachOrdered(target::sendMessage);

        player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Sent a teleport request to &e{0}", target.getName()));
    }

    @Command(name = {"tpahere", "etpahere"}, playerOnly = true)
    public void tpahere(CommandArgs args) {
        Player player = args.getPlayer();

        if (plugin.getServerType() == ServerType.PRISON) {
            player.sendMessage(CC.RED + "You cannot teleport to players in Prison!");
            return;
        }

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/tpahere <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if (target == null) {
            player.sendMessage(CC.RED + args.getArg(0) + " is not online!");
            return;
        }

        if (player == target) {
            player.sendMessage(CC.RED + "You can't teleport to yourself");
            return;
        }

        TSMCUser user = TSMCUser.fromPlayer(target);
        if (!user.getSetting(PlayerSetting.TELEPORT_REQUESTS)) {
            player.sendMessage(CC.translate("&c{0} has teleport requests disabled!", target.getName()));
            return;
        }

        if (user.isIgnored(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You cannot teleport to this player!");
            return;
        }

        LocalPlayer localTarget = plugin.getLocalPlayerManager().getPlayer(target);
        localTarget.requestTeleport(player, true);

        Stream.of(
                CC.translate("&e&lTELEPORT &6■ &e{0} &7has requested to teleport &eto them", player.getName()),
                CC.translate("&e&lTELEPORT &6■ &7To teleport, type &e/tpaccept"),
                CC.translate("&e&lTELEPORT &6■ &7To deny this request, type &e/tpdeny"),
                CC.translate("&e&lTELEPORT &6■ &7This request will timeout after &e60 seconds")
        ).forEachOrdered(target::sendMessage);

        player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Sent a teleport request to &e{0}", target.getName()));
    }

    @Command(name = {"tpaccept", "etpaccept", "tpyes", "etpyes"}, playerOnly = true)
    public void accept(CommandArgs args) {
        Player player = args.getPlayer();
        LocalPlayer localPlayer = plugin.getLocalPlayerManager().getPlayer(player);

        UUID senderUUID = localPlayer.getTeleportRequest();
        if (senderUUID == null) {
            player.sendMessage(CC.RED + "You have no teleport request!");
            return;
        }

        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null || !sender.isOnline()) {
            player.sendMessage(CC.RED + "You have no teleport request!");
            return;
        }

        if (args.length() > 0 && !args.getArg(0).contains(sender.getName())) {
            player.sendMessage(CC.RED + "You have no teleport request!");
            return;
        }

        // 2 minute expiry time
        if (TimeUtils.elapsed(localPlayer.getTeleportRequestTime(), 60 * 1000)) {
            localPlayer.requestTeleport(null, false);
            player.sendMessage(CC.RED + "Your teleport request timed out, try again!");
            return;
        }

        if (localPlayer.isTpRequestHere()) {
            new TimedTeleport.Builder(player, localPlayer.getTpRequestLocation())
                    .targetPlayer(player)
                    .whenComplete(() -> {
                        player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting to &e{0}", sender.getName()));
                        sender.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting &e{0}", player.getName()));
                    })
                    .build();

            sender.sendMessage(CC.translate("&e&lTELEPORT &6■ &e{0} &7accepted your request", player.getName()));

        } else {
            new TimedTeleport.Builder(sender, player.getLocation())
                    .targetPlayer(player)
                    .whenComplete(() -> {
                        sender.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting to &e{0}", player.getName()));
                        player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting &e{0}", sender.getName()));
                    })
                    .build();

            player.sendMessage(CC.translate("&e&lTELEPORT &6■ &e{0} &7accepted your request", sender.getName()));
        }

        localPlayer.requestTeleport(null, false);
    }

    @Command(name = {"tpdeny", "etpdeny", "tpno", "tpno"}, playerOnly = true)
    public void deny(CommandArgs args) {
        LocalPlayer player = plugin.getLocalPlayerManager().getPlayer(args.getPlayer());

        Player sender = Bukkit.getPlayer(player.getTeleportRequest());
        if (sender == null) {
            player.getPlayer().sendMessage(CC.RED + "You have no teleport request!");
            return;
        }

        player.requestTeleport(null, false);

        player.getPlayer().sendMessage(CC.translate("&e&lTELEPORT &6■ &7You denied &e{0}''s &7teleport request", sender.getName()));
        sender.sendMessage(CC.translate("&e&lTELEPORT &6■ &e{0} &7denied your teleport request", player.getPlayer().getName()));
    }
}
