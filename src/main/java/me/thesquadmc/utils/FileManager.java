package me.thesquadmc.utils;

import me.thesquadmc.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public final class FileManager {

	private Main main;
	private File networkingFile;
	private FileConfiguration networkingConfig;

	public FileManager(Main main) {
		this.main = main;
	}

	public void setup() {
		System.out.println("[StaffTools] Setting up config...");
		if (!main.getDataFolder().exists()) {
			System.out.println("[StaffTools] Data Folder not found!");
			System.out.println("[StaffTools] Creating Data Folder...");
			main.getDataFolder().mkdir();
			System.out.println("[StaffTools] Data Folder created!");
		}
		System.out.println("[StaffTools] Loading networking.yml...");
		boolean isNewFile = false;
		this.networkingFile = new File(main.getDataFolder(), "networking.yml");
		if ((!this.networkingFile.exists())) {
			try {
				System.out.println("[StaffTools] File networking.yml not found!");
				System.out.println("[StaffTools] Creating networking.yml...");
				this.networkingFile.createNewFile();
				isNewFile = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("-=-=-=-=- StaffTools -=-=-=-=-");
				System.out.println();
				System.out.println("Uh oh! An exception was thrown!");
				System.out.println("Ensure your server has permission to perform the following actions:");
				System.out.println("Create files, delete files, modify files");
				System.out.println();
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			}
		}
		System.out.println("[StaffTools] File networking.yml loaded!");
		System.out.println("[StaffTools] Setting up yaml config from networking file...");
		this.networkingConfig = YamlConfiguration.loadConfiguration(this.networkingFile);
		System.out.println("[StaffTools] The config for networking.yml has successfully been loaded!");
		if (isNewFile) {
			System.out.println("[StaffTools] File networking.yml has no content!");
			System.out.println("[StaffTools] Creating a default config...");
			networkingConfig.set("redis.host", "0.0.0.0");
			networkingConfig.set("redis.port", 6379);
			networkingConfig.set("redis.password", "");
			System.out.println("[StaffTools] Default networking created!");
			System.out.println("[StaffTools] Saving networking file...");
			saveNetworkingConfig();
			System.out.println("[StaffTools] Saved default networking config!");
			System.out.println("[StaffTools] Reloading networking config...");
			reloadNetworkingConfig();
			System.out.println("[StaffTools] Config reloaded!");
		}
		System.out.println("[StaffTools] File Manager setup and loaded!");
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
