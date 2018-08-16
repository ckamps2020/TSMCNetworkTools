package com.thesquadmc.networktools.warp;

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
            try {
                if (loadFile.exists()) {
                    Warp warp = JSONUtils.getGson().fromJson(
                            new FileReader(loadFile),
                            Warp.class
                    );

                    warps.add(warp);
                }

            } catch (IOException ignored) {
            }
        }

        plugin.getLogger().info(MessageFormat.format("Loaded in {0} warps!", warps.size()));
    }

    public void saveWarps() {
        File file = new File(plugin.getDataFolder(), "warps");

        warps.forEach(warp -> {
            try (FileWriter writer = new FileWriter(new File(file, warp.getName() + ".json"))) {
                JSONUtils.getGson().toJson(
                        warp,
                        writer
                );

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

