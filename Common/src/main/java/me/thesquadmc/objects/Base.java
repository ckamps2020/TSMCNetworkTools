package me.thesquadmc.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class Base {

	private String worldName;
	private Location topLoc;
	private Location bottomLoc;
	private Vector minimumPoint, maximumPoint;

	public Base(Location loc1, Location loc2) {
		this.topLoc = loc1;
		this.bottomLoc = loc2;
		if (loc1 != null && loc2 != null) {
			if (loc1.getWorld() != null && loc2.getWorld() != null) {
				if (!loc1.getWorld().getUID().equals(loc2.getWorld().getUID()))
					throw new IllegalStateException("The 2 locations of the region must be in the same world!");
			} else {
				throw new NullPointerException("One/both of the worlds is/are null!");
			}
			this.worldName = loc1.getWorld().getName();

			double xPos1 = Math.min(loc1.getX(), loc2.getX());
			double yPos1 = Math.min(loc1.getY(), loc2.getY());
			double zPos1 = Math.min(loc1.getZ(), loc2.getZ());
			double xPos2 = Math.max(loc1.getX(), loc2.getX());
			double yPos2 = Math.max(loc1.getY(), loc2.getY());
			double zPos2 = Math.max(loc1.getZ(), loc2.getZ());
			this.minimumPoint = new Vector(xPos1, yPos1, zPos1);
			this.maximumPoint = new Vector(xPos2, yPos2, zPos2);
		} else {
			throw new NullPointerException("One/both of the locations is/are null!");
		}
	}

	public int getWidth() {
		Vector min = minimumPoint;
		Vector max = maximumPoint;

		return (int) (max.getX() - min.getX() + 1);
	}

	public int getHeight() {
		Vector min = minimumPoint;
		Vector max = maximumPoint;

		return (int) (max.getY() - min.getY() + 1);
	}

	public int getLength() {
		Vector min = minimumPoint;
		Vector max = maximumPoint;

		return (int) (max.getZ() - min.getZ() + 1);
	}

	public List<Block> getWalls() {

		List<Block> blocks = new ArrayList<>();

		int x1 = topLoc.getBlockX();
		int y1 = topLoc.getBlockY();
		int z1 = topLoc.getBlockZ();

		int x2 = bottomLoc.getBlockX();
		int y2 = bottomLoc.getBlockY();
		int z2 = bottomLoc.getBlockZ();

		World world = getWorld();

		for (int xPoint = x1; xPoint <= x2; xPoint++) {
			for (int yPoint = y1; yPoint <= y2; yPoint++) {
				Block currentBlock = world.getBlockAt(xPoint, yPoint, z1);
				blocks.add(currentBlock);
			}
		}
		for (int xPoint = x1; xPoint <= x2; xPoint++) {
			for (int yPoint = y1; yPoint <= y2; yPoint++) {
				Block currentBlock = world.getBlockAt(xPoint, yPoint, z2);
				blocks.add(currentBlock);
			}
		}
		for (int zPoint = z1; zPoint <= z2; zPoint++) {
			for (int yPoint = y1; yPoint <= y2; yPoint++) {
				Block currentBlock = world.getBlockAt(x1, yPoint, zPoint);
				blocks.add(currentBlock);
			}
		}
		for (int zPoint = z1; zPoint <= z2; zPoint++) {
			for (int yPoint = y1; yPoint <= y2; yPoint++) {
				Block currentBlock = world.getBlockAt(x2, yPoint, zPoint);
				blocks.add(currentBlock);
			}
		}
		return blocks;
	}

	private boolean isInBorder(Location center, Location notCenter, int range) {
		int x = center.getBlockX(), z = center.getBlockZ();
		int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();
        return x1 < (x + range) && z1 < (z + range) && x1 > (x - range) && z1 > (z - range);
    }

	public List<Player> getNearbyPlayers(Location where) {
		List<Player> found = new ArrayList<>();
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), 25)) {
				if (entity instanceof Player) {
					found.add((Player) entity);
				}
			}
		}
		return found;
	}

	public boolean containsPlayer(Player player) {
		return containsLocation(player.getLocation());
	}

	public boolean containsLocation(Location location) {
		return location != null && location.getWorld().getName().equals(this.worldName) && location.toVector().isInAABB(this.minimumPoint, this.maximumPoint);
	}

	public boolean containsVector(Vector vector) {
		return vector != null && vector.isInAABB(this.minimumPoint, this.maximumPoint);
	}

	public List<Block> getBlocks() {
		List<Block> blockList = new ArrayList<>();
		World world = this.getWorld();
		if (world != null) {
			for (int x = this.minimumPoint.getBlockX(); x <= this.maximumPoint.getBlockX(); x++) {
				for (int y = this.minimumPoint.getBlockY(); y <= this.maximumPoint.getBlockY() && y <= world.getMaxHeight(); y++) {
					for (int z = this.minimumPoint.getBlockZ(); z <= this.maximumPoint.getBlockZ(); z++) {
						blockList.add(world.getBlockAt(x, y, z));
					}
				}
			}
		}
		return blockList;
	}

	public Location getLowerLocation() {
		return this.minimumPoint.toLocation(this.getWorld());
	}

	public double getLowerX() {
		return this.minimumPoint.getX();
	}

	public double getLowerY() {
		return this.minimumPoint.getY();
	}

	public double getLowerZ() {
		return this.minimumPoint.getZ();
	}

	public Location getUpperLocation() {
		return this.maximumPoint.toLocation(this.getWorld());
	}

	public double getUpperX() {
		return this.maximumPoint.getX();
	}

	public double getUpperY() {
		return this.maximumPoint.getY();
	}

	public double getUpperZ() {
		return this.maximumPoint.getZ();
	}

	public double getVolume() {
		return (this.getUpperX() - this.getLowerX() + 1) * (this.getUpperY() - this.getLowerY() + 1) * (this.getUpperZ() - this.getLowerZ() + 1);
	}

	public World getWorld() {
		World world = Bukkit.getServer().getWorld(this.worldName);
		if (world == null) throw new NullPointerException("World '" + this.worldName + "' is not loaded.");
		return world;
	}

	public void setWorld(World world) {
		if (world != null) this.worldName = world.getName();
		else throw new NullPointerException("The world cannot be null.");
	}

	public Location getTopLoc() {
		return topLoc;
	}

	public void setTopLoc(Location topLoc) {
		this.topLoc = topLoc;
	}

	public Location getBottomLoc() {
		return bottomLoc;
	}

	public void setBottomLoc(Location bottomLoc) {
		this.bottomLoc = bottomLoc;
	}

}
