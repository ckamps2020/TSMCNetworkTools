package com.thesquadmc.networktools.kit;

import com.google.common.collect.Sets;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class KitManager {

    private final NetworkTools plugin;

    private final Set<Kit> kits = Sets.newHashSet();

    public KitManager(NetworkTools plugin) {
        this.plugin = plugin;

        File file = new File(plugin.getDataFolder(), "kits");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        File[] files = file.listFiles();
        for (File loadFile : files) {
            if (!loadFile.exists()) continue;

            YamlConfiguration kit = new YamlConfiguration();

            try {
                kit.load(loadFile);

                String name = kit.getString("name");
                long cooldown = kit.getLong("cooldwon");
                List<ItemStack> items = (List<ItemStack>) kit.getList("items");

                addKit(new Kit(name, cooldown, items));
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }

        plugin.getLogger().info(MessageFormat.format("Loaded in {0} kits!", kits.size()));
    }

    public void saveKits() {
        File file = new File(plugin.getDataFolder(), "kits");
        file.delete();
        file.mkdirs();

        kits.forEach(kit -> {
            File kitFile = new File(file, kit.getName() + ".yml");

            YamlConfiguration config = new YamlConfiguration();
            config.set("name", kit.getName());
            config.set("cooldown", kit.getCooldown());

            List<Map<String, Object>> items = kit.getItems().stream()
                    .map(ItemStack::serialize)
                    .collect(Collectors.toList());
            config.set("items", items);

            try {
                config.save(kitFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addKit(Kit kit) {
        kits.add(kit);
    }

    public void clearKits() {
        kits.clear();
    }

    public void removeKit(Kit kit) {
        kits.remove(kit);
    }

    public Optional<Kit> getKit(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return Optional.of(kit);
            }
        }

        return Optional.empty();
    }

    public Set<Kit> getKits() {
        return Collections.unmodifiableSet(kits);
    }
}

