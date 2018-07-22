package me.thesquadmc.commands;

import com.google.common.collect.Maps;
import me.thesquadmc.NetworkTools;
import me.thesquadmc.player.PlayerSetting;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.player.local.LocalPlayer;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.player.LocationUtil;
import me.thesquadmc.utils.time.TimeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class TeleportCommand {

    private static final int DEFAUFT = Integer.MIN_VALUE;

    private final NetworkTools plugin;
    private final Map<UUID, Location> locations = Maps.newHashMap();

    public TeleportCommand(NetworkTools plugin) {
        this.plugin = plugin;
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

                    teleport((Player) sender, target);
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

                    Player where = Bukkit.getPlayer(args.getArg(1));
                    if (where == null) {
                        sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                        return;
                    }

                    teleport(toTeleport, where);
                    sender.sendMessage(CC.B_YELLOW + "TELEPORT " + Unicode.SQUARE + CC.GRAY + " Teleported " + toTeleport.getName() + " to " + where.getName());

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

    @Command(name = {"tpa", "tpask", "tprequest"}, playerOnly = true)
    public void tpa(CommandArgs args) {
        Player player = args.getPlayer();

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
            player.sendMessage(CC.RED + "Find some friends to teleport to, loser!");
            return;
        }

        TSMCUser user = TSMCUser.fromPlayer(target);
        if (!user.getSetting(PlayerSetting.TELEPORT_REQUESTS)) {
            player.sendMessage(CC.translate("&c{0} has teleport requests disabled!", target.getName()));
            return;
        }

        //TODO Check if player is ignored

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

    @Command(name = {"tpahere"}, playerOnly = true)
    public void tpahere(CommandArgs args) {
        Player player = args.getPlayer();

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
            player.sendMessage(CC.RED + "Find some friends to teleport to, loser!");
            return;
        }

        TSMCUser user = TSMCUser.fromPlayer(target);
        if (!user.getSetting(PlayerSetting.TELEPORT_REQUESTS)) {
            player.sendMessage(CC.translate("&c{0} has teleport requests disabled!", target.getName()));
            return;
        }

        //TODO Check if player is ignored

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

    @Command(name = {"tpaccept", "tpayes"}, playerOnly = true)
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
        System.out.println((System.currentTimeMillis() - localPlayer.getTeleportRequestTime()) / 100);
        if (TimeUtils.elapsed(System.currentTimeMillis(), 60 * 1000)) {
            localPlayer.requestTeleport(null, false);
            player.sendMessage(CC.RED + "Your teleport request timed out!");
            return;
        }

        if (localPlayer.isTpRequestHere()) {
            Location location = localPlayer.getTpRequestLocation();
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

            player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting to &e{0}", sender.getName()));
            sender.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting &e{0}", player.getName()));

        } else {
            sender.teleport(player.getLocation());

            player.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting to &e{0}", player.getName()));
            sender.sendMessage(CC.translate("&e&lTELEPORT &6■ &7Teleporting &e{0}", sender.getName()));
        }

        localPlayer.requestTeleport(null, false);
    }

    @Command(name = {"tpadeny", "tpano"}, playerOnly = true)
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

    private boolean teleport(Player teleporting, Player where) {
        return teleport(teleporting, where.getLocation());
    }

    private boolean teleport(Player teleporting, Location to) {
        if (LocationUtil.isBlockUnsafe(to)) {
            return false;
        }

        locations.put(teleporting.getUniqueId(), teleporting.getLocation());
        teleporting.teleport(to);

        return true;
    }

}
