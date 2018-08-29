package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.message.ClickableMessage;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.warp.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ConvertCommand {

    private final NetworkTools plugin;

    public ConvertCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"convertwarps"}, permission = "group.manager", playerOnly = true)
    public void warps(CommandArgs args) {
        Player player = args.getPlayer();

        new ClickableMessage(
                player,
                CC.B_RED + "Are you sure you want to do this? This will remove existing warps!",
                CC.GRAY + "Click on the message if you wish to continue!",
                p -> {
                    plugin.getWarpManager().clearWarps();
                    p.sendMessage(CC.GRAY + "Attempting to load in current warps...");

                    File file = new File(Bukkit.getWorldContainer(), "plugins" + File.separator + "Essentials" + File.separator + "warps");
                    if (!file.exists() || !file.isDirectory()) {
                        p.sendMessage(CC.RED + "Essentials warps folder does not exist or is not a directory!");
                        return;
                    }

                    File[] files = file.listFiles();
                    for (File loadFile : files) {
                        if (!loadFile.exists()) continue;

                        YamlConfiguration warp = new YamlConfiguration();
                        try {
                            warp.load(loadFile);

                            String name = warp.getString("name");

                            String world = warp.getString("world");
                            if (Bukkit.getWorld(world) == null) {
                                p.sendMessage(CC.GRAY + world + " is not a loaded world!");
                                continue;
                            }

                            double x = warp.getDouble("x");
                            double y = warp.getDouble("y");
                            double z = warp.getDouble("z");

                            Location location = new Location(Bukkit.getWorld(world), x, y, z);

                            plugin.getWarpManager().addWarp(new Warp(name, location));
                            p.sendMessage(CC.GRAY + "Added a new warp called " + name);

                        } catch (InvalidConfigurationException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    p.sendMessage((CC.GRAY + "Warps converted: " + plugin.getWarpManager().getWarps().size()));
                }
        ).send();
    }
}
