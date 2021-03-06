package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.TimedTeleport;
import com.thesquadmc.networktools.warp.Warp;
import org.bukkit.Bukkit;
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
                player.sendMessage(CC.translate("&e&lWARP &6■ &7Warps ({0}): &e{1}", plugin.getWarpManager().getWarps().size(), warps));
            }

        } else {
            Optional<Warp> optionalWarp = plugin.getWarpManager().getWarp(args.getArg(0));
            if (optionalWarp.isPresent()) {
                Warp warp = optionalWarp.get();

                if (!player.hasPermission("essentials.warp." + warp.getName().toLowerCase())) {
                    player.sendMessage(CC.RED + "You do not have permission to go to this warp!");
                    return;
                }

                new TimedTeleport.Builder(player, optionalWarp.get().getLocation())
                        .whenComplete(() -> player.sendMessage(CC.translate("&e&lWARP &6■ &7Teleported to &ewarp {0}", optionalWarp.get().getName())))
                        .build();

            } else {
                player.sendMessage(CC.RED + "Could not find a warp with that name!");
            }

        }
    }

    @Command(name = {"spawn"}, playerOnly = true)
    public void spawn(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.spawn.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));

            if (target == null) {
                player.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                return;
            }

            Optional<Warp> warp = plugin.getWarpManager().getWarp("spawn");
            if (warp.isPresent()) {
                new TimedTeleport.Builder(target, warp.get().getLocation())
                        .whenComplete(() -> {
                            target.sendMessage(CC.translate("&e&lWARP &6■ &7Teleported to &espawn"));
                            player.sendMessage(CC.translate("&e&lWARP &6■ &7Teleported &e{0} &7to &espawn", target.getName()));
                        })
                        .build();

            } else {
                player.sendMessage(CC.RED + "There is no warp to spawn set!");
            }

        } else {
            Optional<Warp> warp = plugin.getWarpManager().getWarp("spawn");
            if (warp.isPresent()) {
                new TimedTeleport.Builder(player, warp.get().getLocation())
                        .whenComplete(() -> player.sendMessage(CC.translate("&e&lWARP &6■ &7Teleported to &espawn")))
                        .build();

            } else {
                player.sendMessage(CC.RED + "There is no warp to spawn set!");
            }
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

