package com.thesquadmc.objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Config {

	private FileConfiguration config;
	private File configFile;

	protected boolean wasCreated;

	public Config(String name, JavaPlugin plugin) {
		this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
		if (!this.configFile.exists()) {
			try {
				this.configFile.getParentFile().mkdirs();
				this.configFile.createNewFile();
				this.wasCreated = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	public void save() {
		try {
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public File getConfigFile() {
		return configFile;
	}

	public boolean isWasCreated() {
		return wasCreated;
	}

}
