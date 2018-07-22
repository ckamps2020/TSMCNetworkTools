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

/**
 * This class handles all warps/spawns
 * provided in the locations.yml file.
 */
public class KitManager {

    private final NetworkTools plugin;

    private final Set<Kit> warps = Sets.newHashSet();

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

                    warps.add(kit);
                }

            } catch (IOException ignored) {
            }
        }

        plugin.getLogger().info(MessageFormat.format("Loaded in {0} warps!", warps.size()));
    }

    public void saveWarps() {
        File file = new File(plugin.getDataFolder(), "kits");

        warps.forEach(kit -> {
            try (FileWriter writer = new FileWriter(new File(file, kit.getName() + ".json"))) {
                JSONUtils.getGson().toJson(
                        kit,
                        writer
                );

            } catch (IOException e) {
                plugin.getLogger().severe("Could not save data of user " + player);
                e.printStackTrace();
            }
        });
    }

    public void addWarp(Kit warp) {
        warps.add(warp);
    }

    public void removeWarp(Kit warp) {
        warps.remove(warp);
    }

    public Optional<Kit> getWarp(String name) {
        for (Kit warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return Optional.of(warp);
            }
        }

        return Optional.empty();
    }

    public Set<Kit> getWarps() {
        return Collections.unmodifiableSet(warps);
    }
}

