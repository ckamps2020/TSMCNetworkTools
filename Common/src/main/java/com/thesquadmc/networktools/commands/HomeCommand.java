package com.thesquadmc.networktools.commands;

import com.sgtcaze.nametagedit.NametagEdit;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.player.TimedTeleport;
import me.thesquadmc.utils.msgs.CC;
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
                player.sendMessage(CC.RED + "Your homes: " + String.join(" ", localPlayer.getHomes().keySet()));

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
                    .whenComplete(() -> player.sendMessage(CC.translate("&e&lHOME &6■ &7Teleported to your home: &e" + name)))
                    .build();
        }
    }

    @Command(name = "sethome", playerOnly = true)
    public void sethome(CommandArgs args) {
        Player player = args.getPlayer();
        LocalPlayer localPlayer = plugin.getLocalPlayerManager().getPlayer(player);


    }

    public Set<String> getMultipleHomes() {
        final ConfigurationSection section = getConfig().getConfigurationSection("sethome-multiple");
        return section == null ? null : section.getKeys(false);
    }

    public int getHomeLimit(Player player) {
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

    public int getHomeLimit(final String set) {
        return getConfig().getInt("sethome-multiple." + set, getConfig().getInt("sethome-multiple.default", 1));
    }

    private Configuration getConfig() {
        return NametagEdit.getInstance().getHandler().getConfig();
    }
}
