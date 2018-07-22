package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.warp.Warp;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

public class WarpCommand {

    private final NetworkTools plugin;

    public WarpCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"warp", "warps"}, playerOnly = true)
    public void warp(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            String warps = plugin.getWarpManager().getWarps().stream()
                    .filter(warp -> player.hasPermission("essentials.warp" + warp.getName().toLowerCase()))
                    .map(Warp::getName)
                    .collect(Collectors.joining(", "));

            if (warps == null || warps.isEmpty()) {
                player.sendMessage(CC.translate("&e&lWARP &6■ &7No warps defined"));

            } else {
                player.sendMessage(CC.translate("&e&lWARP &6■ &7Warps: &e{0}", warps));
            }

        } else {
            Optional<Warp> warp = plugin.getWarpManager().getWarp(args.getArg(0));
            if (warp.isPresent()) {
                player.teleport(warp.get().getLocation());

                //TODO Delayed teleport task

            } else {
                player.sendMessage(CC.RED + "Could not find a warp with that name!");
            }

        }
    }

    @Command(name = {"spawn"}, playerOnly = true)
    public void spawn(CommandArgs args) {
        Player player = args.getPlayer();

        Optional<Warp> warp = plugin.getWarpManager().getWarp("spawn");
        if (warp.isPresent()) {
            player.teleport(warp.get().getLocation());

        } else {
            player.sendMessage(CC.RED + "There is no warp to spawn set!");
        }

    }

    @Command(name = {"setwarp"}, permission = "essentials.setwarp", playerOnly = true)
    public void setWarp(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You must specify a name!");
            return;
        }

        String name = args.getArg(0);
        Optional<Warp> warp = plugin.getWarpManager().getWarp(name);
        if (warp.isPresent()) {
            player.sendMessage(CC.RED + "A warp with this name already exists!");
            return;
        }

        plugin.getWarpManager().addWarp(new Warp(name, player.getLocation()));
        player.sendMessage(CC.translate("&e&lWARP &6■ &7Added a warp named &e{0} &7at your &ecurrent location", name));
    }

    @Command(name = {"delwarp"}, permission = "essentials.delwarp", playerOnly = true)
    public void delWarp(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You must specify a name!");
            return;
        }

        Optional<Warp> warp = plugin.getWarpManager().getWarp(args.getArg(0));
        if (warp.isPresent()) {
            plugin.getWarpManager().removeWarp(warp.get());
            player.sendMessage(CC.translate("&e&lWARP &6■ &7Removed &e{0} warp", warp.get().getName()));

        } else {
            player.sendMessage(CC.RED + "Could not find a warp with that name!");
        }
    }
}

