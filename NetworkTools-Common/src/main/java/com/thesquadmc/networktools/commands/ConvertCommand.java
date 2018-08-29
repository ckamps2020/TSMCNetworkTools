package com.thesquadmc.networktools.commands;

import com.google.common.base.Preconditions;
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
import java.nio.file.Files;
import java.nio.file.Path;

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
                    Path path = new File(Bukkit.getWorldContainer(), "plugins" + File.separator + "Essentials" + File.separator + "warps").toPath();
                    if (!Files.exists(path) || !Files.isDirectory(path)) {
                        p.sendMessage(CC.RED + "Essentials warps folder does not exist or is not a directory!");
                        return;
                    }

                    try {
                        Files.walk(path)
                                .filter(file -> file.endsWith(".yml"))
                                .forEach(file -> {
                                    YamlConfiguration warp = new YamlConfiguration();
                                    try {
                                        warp.load(file.toFile());

                                        String name = warp.getString("name");

                                        String world = warp.getString("world");
                                        double x = warp.getDouble("x");
                                        double y = warp.getDouble("y");
                                        double z = warp.getDouble("z");

                                        Location location = new Location(Bukkit.getWorld(world), x, y, z);
                                        Preconditions.checkNotNull(location);

                                        plugin.getWarpManager().addWarp(new Warp(name, location));
                                    } catch (InvalidConfigurationException | IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).send();
    }
}
