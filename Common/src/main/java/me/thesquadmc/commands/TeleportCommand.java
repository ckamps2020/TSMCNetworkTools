package me.thesquadmc.commands;

import com.google.common.collect.Maps;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.player.LocationUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TeleportCommand {

    private static final int DEFAUFT = Integer.MIN_VALUE;

    private final Map<UUID, Location> locations = Maps.newHashMap();

    @Command(name = {"teleport", "tp", "tpo", "ntp"}, permission = "essentials.tp")
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
                    break;
                }
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
                    break;
                }
            }

            case 3: {
                if (sender instanceof Player) {
                    int x = NumberUtils.toInt(args.getArg(0), DEFAUFT);
                    int y = NumberUtils.toInt(args.getArg(0), DEFAUFT);
                    int z = NumberUtils.toInt(args.getArg(0), DEFAUFT);

                    if (x == DEFAUFT || y == DEFAUFT || z == DEFAUFT) {
                        sender.sendMessage(CC.RED + "Not valid coordinates! /tp <x> <y> <z>");
                        return;
                    }

                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    ((Player) sender).teleport(location);
                    break;
                }
            }

            default: {
                sender.sendMessage(CC.B_YELLOW + "TELEPORT " + Unicode.SQUARE + CC.GRAY + " Uh oh, something went wrong!");
            }
        }
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

        TSMCUser user = TSMCUser.fromPlayer(target);
        if (user.getSetting(PlayerSetting.TELEPORT_REQUESTS)) {
        }
    }

    private boolean teleport(Player teleporting, Player where) {
        return teleporting(teleporting, where.getLocation());
    }

    private boolean teleporting(Player teleporting, Location to) {
        if (LocationUtil.isBlockUnsafe(to)) {
            return false;
        }

        locations.put(teleporting.getUniqueId(), teleporting.getLocation());
        teleporting.teleport(to);

        return true;
    }

}
