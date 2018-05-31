package me.thesquadmc.utils.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

	public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
		List<Block> blocks = new ArrayList<>();
		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		for(int x = bottomBlockX; x <= topBlockX; x++) {
			for(int z = bottomBlockZ; z <= topBlockZ; z++) {
				for(int y = bottomBlockY; y <= topBlockY; y++) {
					if (loc1.getWorld() != null && loc1.getWorld().getBlockAt(x, y, z) != null && loc1.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
						Block block = loc1.getWorld().getBlockAt(x, y, z);
						blocks.add(block);
					}
				}
			}
		}
		return blocks;
	}

	public static void placeBed(Location location) {
		BlockState bedFoot = location.getBlock().getRelative(location.getBlock().getFace(location.getBlock())).getState();
		BlockState bedHead = bedFoot.getBlock().getRelative(BlockFace.SOUTH).getState();
		bedFoot.setType(Material.BED_BLOCK);
		bedHead.setType(Material.BED_BLOCK);
		bedFoot.setRawData((byte) 0x0);
		bedHead.setRawData((byte) 0x8);
		bedFoot.update(true, false);
		bedHead.update(true, true);
	}

	public static List<Block> getNearbyBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<Block>();
		for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

}
