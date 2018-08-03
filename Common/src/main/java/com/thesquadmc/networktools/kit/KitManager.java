package com.thesquadmc.networktools.kit;

import com.google.common.collect.Sets;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.json.JSONUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static sun.audio.AudioPlayer.player;

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
            try {
                if (loadFile.exists()) {
                    Kit kit = JSONUtils.getGson().fromJson(
                            new FileReader(loadFile),
                            Kit.class
                    );

                    kits.add(kit);
                }

            } catch (IOException ignored) {
            }
        }

        plugin.getLogger().info(MessageFormat.format("Loaded in {0} kits!", kits.size()));
    }

    public void saveKits() {
        File file = new File(plugin.getDataFolder(), "kits");

        kits.forEach(kit -> {
            try (FileWriter writer = new FileWriter(new File(file, kit.getName() + ".json"))) {
                JSONUtils.getGson().toJson(
                        kit,
                        writer
                );

            } catch (IOException e) {
                plugin.getLogger().severe("Could not save data of kit: " + player);
                e.printStackTrace();
            }
        });
    }

    public void addKit(Kit kit) {
        kits.add(kit);
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

