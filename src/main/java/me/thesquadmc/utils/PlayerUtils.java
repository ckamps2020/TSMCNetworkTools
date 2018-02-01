package me.thesquadmc.utils;

import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public final class PlayerUtils {

	private static boolean isInBorder(Location center, Location notCenter, int range) {
		int x = center.getBlockX(), z = center.getBlockZ();
		int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();
		if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range)) {
			return false;
		}
		return true;
	}

	public static List<Player> getNearbyPlayers(Location where, int range) {
		List<Player> found = new ArrayList<>();
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), range)) {
				if (entity instanceof Player) {
					found.add((Player) entity);
				}
			}
		}
		return found;
	}

	public static void hidePlayerSpectatorStaff(Player player) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (!isEqualOrHigherThen(p, Rank.TRAINEE)) {
				p.hidePlayer(player);
			}
		}
	}

	public static void hidePlayerSpectatorYT(Player player) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
	}

	public static void showPlayerSpectator(Player player) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
	}

	public static void unfreezePlayer(Player player) {
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		player.removePotionEffect(PotionEffectType.JUMP);
	}

	public static void freezePlayer(Player player) {
		player.setWalkSpeed(0);
		player.setFlySpeed(0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200, false, false));
	}

	public static void sendActionBarToPlayer(String actionBar, Player player) {
		Packet<?> actionBarPacket = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + StringUtils.msg(actionBar) + "\"}"), (byte)2);
		CraftPlayer craftPlayer = (CraftPlayer) player;
		PlayerConnection connection = craftPlayer.getHandle().playerConnection;
		connection.sendPacket(actionBarPacket);
	}

	public static boolean isEqualOrHigherThen(Player player, Rank rank) {
		User user = Main.getMain().getLuckPermsApi().getUser(player.getUniqueId());
		if (user.getPrimaryGroup() != null) {
			for (Rank r : Rank.values()) {
				if (r.getName().equalsIgnoreCase(user.getPrimaryGroup())) {
					if (r.getPriority() >= rank.getPriority()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean doesRankMatch(Player player, Rank rank) {
		User user = Main.getMain().getLuckPermsApi().getUser(player.getUniqueId());
		if (user.getPrimaryGroup() != null) {
			for (Rank r : Rank.values()) {
				if (r.getName().equalsIgnoreCase(user.getPrimaryGroup())) {
					if (r.getName().equalsIgnoreCase(rank.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean hasPermission(Group group, String permission) {
		return group.getPermissions().stream()
				.filter(Node::getValue)
				.filter(Node::isPermanent)
				.filter(n -> !n.isServerSpecific())
				.filter(n -> !n.isWorldSpecific())
				.anyMatch(n -> n.getPermission().startsWith(permission));
	}

}
