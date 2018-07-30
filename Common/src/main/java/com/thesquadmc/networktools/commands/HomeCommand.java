package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.player.TimedTeleport;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
            if (localPlayer.getHomesSize() == 0) {
                player.sendMessage(CC.RED + "You have not set any homes!");

            } else {
                Location location = localPlayer.getHome("home");
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
}
