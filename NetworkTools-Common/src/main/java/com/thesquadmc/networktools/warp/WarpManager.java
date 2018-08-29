package com.thesquadmc.networktools.warp;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * This class handles all warps/spawns
 * provided in the locations.yml file.
 */
public class WarpManager {

    private final NetworkTools plugin;

    private final Set<Warp> warps = Sets.newHashSet();

    public WarpManager(NetworkTools plugin) {
        this.plugin = plugin;

        File file = new File(plugin.getDataFolder(), "warps");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        File[] files = file.listFiles();
        for (File loadFile : files) {
            if (!loadFile.exists()) continue;

            YamlConfiguration warp = new YamlConfiguration();
            try {
                warp.load(loadFile);

                String name = warp.getString("name");
                UUID uuid = (UUID) warp.get("world-uuid");
                int x = warp.getInt("x");
                int y = warp.getInt("y");
                int z = warp.getInt("z");

                Location location = new Location(Bukkit.getWorld(uuid), x, y, z);
                Preconditions.checkNotNull(location);

                addWarp(new Warp(name, location));
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }

        }

        plugin.getLogger().info(MessageFormat.format("Loaded in {0} warps!", warps.size()));
    }

    public void saveWarps() {
        File file = new File(plugin.getDataFolder(), "warps");

        warps.forEach(warp -> {
            File warpFile = new File(file, warp.getName() + ".yml");

            YamlConfiguration config = new YamlConfiguration();
            config.set("name", warp.getName());

            Location location = warp.getLocation();
            config.set("world-uuid", location.getWorld().getUID());
            config.set("x", location.getBlockX());
            config.set("y", location.getBlockY());
            config.set("z", location.getBlockZ());

            try {
                config.save(warpFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addWarp(Warp warp) {
        warps.add(warp);
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public Optional<Warp> getWarp(String name) {
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return Optional.of(warp);
            }
        }

        return Optional.empty();
    }

    public Set<Warp> getWarps() {
        return Collections.unmodifiableSet(warps);
    }
}

