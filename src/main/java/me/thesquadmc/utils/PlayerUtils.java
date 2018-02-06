package me.thesquadmc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

	public static void setName(Player player, String name) {
		try {
			GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
			Field ff = playerProfile.getClass().getDeclaredField("name");
			ff.setAccessible(true);
			ff.set(playerProfile, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Property getSkinProperty(String name) {
		try {
			InputStream input = new URL("https://use.gameapis.net/mc/player/profile/" + name).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			Stream<String> content = reader.lines();
			content.forEach(string -> {
				if (string.toLowerCase().startsWith("            \"value\": ")) {
					//key value
					String a = string.replaceAll("\"", "");
					String b = a.replaceAll(":", "");
					String c = b.replaceAll(" ", "");
					String d = c.replaceAll("value", "");
					String e = d.replaceAll(",", "");
					Main.getMain().setValue(e);
				} else if (string.toLowerCase().startsWith("            \"signature\": ")) {
					//sig
					String a = string.replaceAll("\"", "");
					String b = a.replaceAll(":", "");
					String c = b.replaceAll(" ", "");
					String d = c.replaceAll("signature", "");
					String e = d.replaceAll(",", "");
					Main.getMain().setSig(e);
				}
			});
			reader.close();
			input.close();
			return new Property("textures", Main.getMain().getValue(), Main.getMain().getSig());
		} catch (Exception e) {
			System.out.println("Unable to get skin");
			return null;
		}
	}

	public static void updateGlobalSkin(String name) {
		try {
			InputStream input = new URL("https://use.gameapis.net/mc/player/profile/" + name).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			Stream<String> content = reader.lines();
			content.forEach(string -> {
				if (string.toLowerCase().startsWith("            \"value\": ")) {
					//key value
					String a = string.replaceAll("\"", "");
					String b = a.replaceAll(":", "");
					String c = b.replaceAll(" ", "");
					String d = c.replaceAll("value", "");
					String e = d.replaceAll(",", "");
					Main.getMain().setValue(e);
				} else if (string.toLowerCase().startsWith("            \"signature\": ")) {
					//sig
					String a = string.replaceAll("\"", "");
					String b = a.replaceAll(":", "");
					String c = b.replaceAll(" ", "");
					String d = c.replaceAll("signature", "");
					String e = d.replaceAll(",", "");
					Main.getMain().setSig(e);
				}
			});
			reader.close();
			input.close();
		} catch (Exception e) {
			System.out.println("Unable to get skin");
		}
	}

	public static void removePlayerTextures(Player player) {
		hidePlayerSpectatorYT(player);
		CraftPlayer p = (CraftPlayer) player;
		p.getProfile().getProperties().removeAll("textures");
		showPlayerSpectator(player);
	}

	public static void restorePlayerTextures(Player player) {
		hidePlayerSpectatorYT(player);
		TempData tempData = Main.getMain().getTempDataManager().getTempData(player.getUniqueId());
		CraftPlayer p = (CraftPlayer) player;
		removePlayerTextures(player);
		p.getProfile().getProperties().put("textures", new Property("textures", tempData.getSkinkey(), tempData.getSignature()));
		showPlayerSpectator(player);
	}

	public static void setSkin(Player player, String playerSkin) {
		hidePlayerSpectatorYT(player);
		((CraftPlayer) player).getHandle().getProfile().getProperties().removeAll("textures");
		Property property = getSkinProperty(playerSkin);
		if (property != null) {
			((CraftPlayer) player).getHandle().getProfile().getProperties().put("textures", property);
		}
		showPlayerSpectator(player);
	}

	public static void setSameSkin(Player player) {
		hidePlayerSpectatorYT(player);
		((CraftPlayer) player).getHandle().getProfile().getProperties().removeAll("textures");
		Property property = new Property("textures", Main.getMain().getValue(), Main.getMain().getSig());
		if (property != null) {
			((CraftPlayer) player).getHandle().getProfile().getProperties().put("textures", property);
		}
		showPlayerSpectator(player);
	}

	public static void giveMeTheirItem(Player player, Player target, ItemStack itemStack) {
		if (target != null) {
			if (target.getInventory() != null) {
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					ItemStack stack = player.getInventory().getItem(i);
					if (stack != null && stack.getType() != Material.AIR) {
						player.getInventory().addItem(itemStack);
						target.getInventory().remove(stack);
					}
				}
			}
		}
	}

	public static double getArmorLevel(Player player) {
		org.bukkit.inventory.PlayerInventory inv = player.getInventory();
		ItemStack helmet = null;
		ItemStack boots = null;
		ItemStack chest = null;
		ItemStack pants = null;

		if(inv.getBoots() != null){boots = inv.getBoots();}
		if(inv.getBoots() == null){boots = new ItemStack(Material.LEATHER_BOOTS);}
		if (inv.getHelmet() != null){helmet = inv.getHelmet(); }
		if(inv.getHelmet() == null){helmet = new ItemStack(Material.LEATHER_HELMET);}
		if (inv.getChestplate() != null){chest = inv.getChestplate();}
		if(inv.getChestplate() == null){chest = new ItemStack(Material.LEATHER_CHESTPLATE);}
		if (inv.getLeggings() != null){pants = inv.getLeggings();}
		if(inv.getLeggings() == null){pants = new ItemStack(Material.LEATHER_LEGGINGS);}
		double red = 0.0;
		if (helmet.getType() == null || helmet.getType() == Material.AIR)red = red + 0.0;
		else if(helmet != null && helmet.getType() == Material.LEATHER_HELMET)red = red + 0.04;
		else if(helmet != null && helmet.getType() == Material.GOLD_HELMET)red = red + 0.08;
		else if(helmet != null && helmet.getType() == Material.CHAINMAIL_HELMET)red = red + 0.08;
		else if(helmet != null && helmet.getType() == Material.IRON_HELMET)red = red + 0.08;
		else if(helmet != null && helmet.getType() == Material.DIAMOND_HELMET)red = red + 0.12;
		//
		if (boots.getType() == null || boots.getType() == Material.AIR)red = red + 0;
		else if(boots != null && boots.getType() == Material.LEATHER_BOOTS)red = red + 0.04;
		else if(boots != null && boots.getType() == Material.GOLD_BOOTS)red = red + 0.04;
		else if(boots != null && boots.getType() == Material.CHAINMAIL_BOOTS)red = red + 0.04;
		else if(boots != null && boots.getType() == Material.IRON_BOOTS)red = red + 0.08;
		else if(boots != null && boots.getType() == Material.DIAMOND_BOOTS)red = red + 0.12;
		//
		if (pants.getType() == null || pants.getType() == Material.AIR)red = red + 0;
		else if(pants != null && pants.getType() == Material.LEATHER_LEGGINGS)red = red + 0.08;
		else if(pants != null && pants.getType() == Material.GOLD_LEGGINGS)red = red + 0.12;
		else if(pants != null && pants.getType() == Material.CHAINMAIL_LEGGINGS)red = red + 0.16;
		else if(pants != null && pants.getType() == Material.IRON_LEGGINGS)red = red + 0.20;
		else if(pants != null && pants.getType() == Material.DIAMOND_LEGGINGS)red = red + 0.24;
		//
		if (chest.getType() == null || chest.getType() == Material.AIR)red = red + 0;
		else if(chest != null && chest.getType() == Material.LEATHER_CHESTPLATE)red = red + 0.12;
		else if(chest != null && chest.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
		else if(chest != null && chest.getType() == Material.CHAINMAIL_CHESTPLATE)red = red + 0.20;
		else if(chest != null && chest.getType() == Material.IRON_CHESTPLATE)red = red + 0.24;
		else if(chest != null && chest.getType() == Material.DIAMOND_CHESTPLATE)red = red + 0.32;
		return red;
	}

}
