package com.thesquadmc.networktools.commands;

import com.sgtcaze.nametagedit.NametagEdit;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.message.ClickableMessageBuilder;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.LocationUtil;
import com.thesquadmc.networktools.utils.player.TimedTeleport;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class HomeCommand {

    private final NetworkTools plugin;

    public HomeCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"home", "homes"}, playerOnly = true)
    public void home(CommandArgs args) {
        Player player = args.getPlayer();
        LocalPlayer localPlayer = plugin.getLocalPlayerManager().getPlayer(player.getUniqueId());

        if (args.length() == 0) {
            if (localPlayer.getHomes().size() == 0) {
                player.sendMessage(CC.RED + "You have not set any homes!");

            } else if (localPlayer.getHomes().size() > 1) {
                player.sendMessage(CC.RED + "You have multiple homes, you must specify one!");
                player.sendMessage(CC.RED + "Your homes: " + String.join(", ", localPlayer.getHomes().keySet()));

            } else {
                Location location = localPlayer.getHomes().values().stream().findFirst().get();
                new TimedTeleport.Builder(player, location)
                        .whenComplete(() -> player.sendMessage(CC.translate("&e&lHOME &6■ &7Teleported to your home!")))
                        .build();
            }

        } else {
            String name = args.getArg(0);
            Location location = localPlayer.getHome(name);

            if (location == null) {
                player.sendMessage(CC.RED + "You do not have a home named " + name);
                return;
            }

            new TimedTeleport.Builder(player, location)
                    .whenComplete(() -> player.sendMessage(CC.translate("&e&lHOME &6■ &7Teleported to your home: &e{0}", name)))
                    .build();
        }
    }

    @Command(name = "sethome", playerOnly = true)
    public void sethome(CommandArgs args) {
        Player player = args.getPlayer();
        LocalPlayer localPlayer = plugin.getLocalPlayerManager().getPlayer(player);

        String name = "home";
        if (args.length() > 0) {
            name = args.getArg(0);
        }

        int limit = getHomeLimit(player);
        if (!player.hasPermission("essentials.sethome.multiple.unlimited") && localPlayer.getHomes().size() >= limit) {
            player.sendMessage(CC.translate("&cYou can only set {0} {1}!", limit, limit == 1 ? "home" : "homes"));
            return;
        }

        Location location = localPlayer.getHome(name);
        if (location == null) {
            localPlayer.addHome(name, LocationUtil.getSafeDestination(player, player.getLocation()));
            player.sendMessage(CC.translate("&e&lHOME &6■ &7Set a home named &e{0} &7at your &ecurrent location", name));

        } else {
            player.sendMessage(CC.translate("&e&lHOME &6■ &7You already have a home named &e&l{0}", name));

            String finalName = name;
            new ClickableMessageBuilder(player)
                    .message(CC.B_GREEN + "[REPLACE]")
                    .hoverMessage(CC.GRAY + "Click if you want to replace your existing home")
                    .onClick(p -> {
                        player.sendMessage(CC.translate("&e&lHOME &6■ &7Set a home named &e{0} &7at your &ecurrent location", finalName));
                        localPlayer.addHome(finalName, player.getLocation());
                    })
                    .complete()

                    .space()

                    .message(CC.B_RED + "[DON'T REPLACE]")
                    .hoverMessage(CC.GRAY + "Click if you want to keep your existing home")
                    .onClick(p -> p.sendMessage(CC.translate("&e&lHOME &6■ &7You did not replace your home called {0}", finalName)))
                    .complete()

                    .send();
        }
    }

    @Command(name = "delhome", playerOnly = true)
    public void on(CommandArgs args) {
        Player player = args.getPlayer();
        LocalPlayer localPlayer = plugin.getLocalPlayerManager().getPlayer(player);

        if (args.length() == 0) {
            if (localPlayer.getHomes().size() == 0) {
                player.sendMessage(CC.RED + "You must specify a name! /delhome <name>");
                return;
            }

            String name = localPlayer.getHomes().keySet().stream().findFirst().get();
            localPlayer.removeHome(name);

            player.sendMessage(CC.translate("&e&lHOME &6■ &7You removed your only home!"));
            return;
        }

        String name = args.getArg(0);
        Location location = localPlayer.removeHome(name);
        if (location == null) {
            player.sendMessage(CC.translate("&cYou have no home named {0}", name));
            return;
        }

        player.sendMessage(CC.translate("&e&lHOME &6■ &7You removed your home named {0}", name));
    }

    private int getHomeLimit(Player player) {
        int limit = 1;
        if (player.hasPermission("essentials.sethome.multiple")) {
            limit = getHomeLimit("default");
        }

        final Set<String> homeList = getMultipleHomes();
        if (homeList != null) {
            for (String set : homeList) {
                if (player.hasPermission("essentials.sethome.multiple." + set) && (limit < getHomeLimit(set))) {
                    limit = getHomeLimit(set);
                }
            }
        }

        return limit;
    }

    private Set<String> getMultipleHomes() {
        final ConfigurationSection section = getConfig().getConfigurationSection("sethome-multiple");
        return section == null ? null : section.getKeys(false);
    }

    private int getHomeLimit(final String set) {
        return getConfig().getInt("sethome-multiple." + set, getConfig().getInt("sethome-multiple.default", 1));
    }

    private Configuration getConfig() {
        return NametagEdit.getInstance().getHandler().getConfig();
    }
}
