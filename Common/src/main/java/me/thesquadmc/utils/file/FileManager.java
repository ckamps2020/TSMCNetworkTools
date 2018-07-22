package me.thesquadmc.utils.file;

import me.thesquadmc.NetworkTools;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class FileManager {

    private NetworkTools plugin;

    private File networkingFile;
    private FileConfiguration networkingConfig;

    public FileManager(NetworkTools networkTools) {
        this.plugin = networkTools;
    }

    public void setup() {
        System.out.println("[NetworkTools] Setting up config...");
        if (!plugin.getDataFolder().exists()) {
            System.out.println("[NetworkTools] Data Folder not found, creating one...");
            plugin.getDataFolder().mkdir();
        }

        System.out.println("[NetworkTools] Loading networking.yml...");
        boolean isNewFile = false;

        networkingFile = new File(plugin.getDataFolder(), "networking.yml");
        if (!this.networkingFile.exists()) {
            try {
                System.out.println("[NetworkTools] File networking.yml not found, creating one...");
                networkingFile.createNewFile();
                isNewFile = true;

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("-=-=-=-=- NetworkTools -=-=-=-=-");
                System.out.println();
                System.out.println("Uh oh! An exception was thrown!");
                System.out.println("Ensure your server has permission to perform the following actions:");
                System.out.println("Create files, delete files, modify files");
                System.out.println();
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            }
        }

        System.out.println("[NetworkTools] File networking.yml loaded!");
        System.out.println("[NetworkTools] Setting up yaml config from networking file...");
        this.networkingConfig = YamlConfiguration.loadConfiguration(this.networkingFile);
        System.out.println("[NetworkTools] The config for networking.yml has successfully been loaded!");

        if (isNewFile) {
            System.out.println("[NetworkTools] File networking.yml has no content, populating the config...");
            networkingConfig.set("redis.host", "0.0.0.0");
            networkingConfig.set("redis.port", 6379);
            networkingConfig.set("redis.password", "");
            networkingConfig.set("mysql.host", "localhost");
            networkingConfig.set("mysql.port", "3306");
            networkingConfig.set("mysql.dbpassword", "");
            networkingConfig.set("mysql.dbname", "friends");
            networkingConfig.set("mysql.dbuser", "root");
            networkingConfig.set("mongo.host", "0.0.0.0");
            networkingConfig.set("mongo.port", 6379);
            networkingConfig.set("mongo.password", "password");
            networkingConfig.set("mongo.user", "user");
            networkingConfig.set("mongo.database", "database");
            networkingConfig.set("buycraft.secret", "KEY_HERE");

            saveNetworkingConfig();
            System.out.println("[NetworkTools] Saved default networking config!");
            System.out.println("[NetworkTools] Reloading networking config...");
            reloadNetworkingConfig();
        }
    }

    public FileConfiguration getNetworkingConfig() {
        return this.networkingConfig;
    }

    public void saveNetworkingConfig() {
        try {
            this.networkingConfig.save(this.networkingFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadNetworkingConfig() {
        this.networkingConfig = YamlConfiguration.loadConfiguration(this.networkingFile);
    }
}
