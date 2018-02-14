package me.thesquadmc.utils.server;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

public final class WorldUtils {

	public static void registerWorld(String worldName) {
		Bukkit.getServer().createWorld(new WorldCreator(worldName));
	}

	public static void unloadWorld(String world) {
		Bukkit.getServer().unloadWorld(world, false);
	}

	public static boolean isWorldLoaded(String world) {
		return Bukkit.getWorld(world) != null;
	}

}
