package com.thesquadmc.networktools.utils.converter;

import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EssentialsPlayerConverter {

    private final NetworkTools plugin;
    private final UUID uuid;
    private final File file;

    private final YamlConfiguration configuration = new YamlConfiguration();

    public EssentialsPlayerConverter(NetworkTools plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.file = new File(Bukkit.getWorldContainer(), "plugins" + File.separator + "Essentials" + File.separator + "userdata" + File.separator + uuid.toString() + ".yml");

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
            plugin.getLocalPlayerManager().getPlayer(uuid).addHome(name, location);
        });
    }

    private void convertNickname() {
        if (!configuration.contains("nickname")) {
            return;
        }

        plugin.getLocalPlayerManager().getPlayer(uuid).setNickname(configuration.getString("nickname"));
    }

}
