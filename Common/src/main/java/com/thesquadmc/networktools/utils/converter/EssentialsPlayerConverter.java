package com.thesquadmc.networktools.utils.converter;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class EssentialsPlayerConverter {

    private final NetworkTools plugin;
    private final LocalPlayer localPlayer;
    private final File file;

    private final YamlConfiguration configuration = new YamlConfiguration();

    public EssentialsPlayerConverter(NetworkTools plugin, LocalPlayer localPlayer) {
        this.plugin = plugin;
        this.localPlayer = localPlayer;
        this.file = new File(Bukkit.getWorldContainer(), "plugins" + File.separator + "Essentials" + File.separator + "userdata" + File.separator + localPlayer.getUUID().toString() + ".yml");

        if (!file.exists()) {
            plugin.getLogger().info(localPlayer.getUsername() + " did not have any Essentials data!");
            return;
        }

        try {
            configuration.load(file);

            convertHomes();
            convertNickname();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void convertHomes() {
        if (!configuration.contains("homes") || !configuration.isConfigurationSection("homes")) {
            return;
        }

        ConfigurationSection section = configuration.getConfigurationSection("homes");
        section.getKeys(false).forEach(name -> {
            ConfigurationSection loc = configuration.getConfigurationSection("homes." + name);

            World world = Bukkit.getWorld(loc.getString("world"));
            if (world == null) {
                plugin.getLogger().info("Cannot convert home file!");
                return;
            }

            double x = loc.getDouble("x");
            double y = loc.getDouble("y");
            double z = loc.getDouble("z");
            float yaw = loc.getLong("float");
            float pitch = loc.getLong("pitch");

            Location location = new Location(world, x, y, z, yaw, pitch);
            localPlayer.addHome(name, location);
        });
    }

    private void convertNickname() {
        if (!configuration.contains("nickname")) {
            return;
        }

        localPlayer.setNickname(configuration.getString("nickname"));
    }

}
