package me.thesquadmc.utils.inventory;

import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.TimeUtils;
import me.thesquadmc.utils.msgs.CC;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ItemUtils {

	public static void spawnItem(World world, ItemStack itemStack, Location location) {
		world.dropItemNaturally(location, itemStack);
	}

	public static void dropInventory(Player player) {
		for (ItemStack s : player.getInventory().getContents()) {
			if (s != null && s.getType() != Material.AIR) {
				Bukkit.getWorld(player.getWorld().getName()).dropItemNaturally(player.getLocation(), s);
			}
		}
	}

	public static ItemStack createPotion(String name, PotionType type, int level, int duration) {
		ItemStack itemStack = new ItemStack(Material.POTION);
		PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

		if (name != null) {
			meta.setDisplayName(name);
		}

		meta.setLore(Arrays.asList(
				"", CC.GRAY + StringUtils.toNiceString(type.name()) + " " + level + " Potion",
				CC.GRAY + "    Duration: " + TimeUtils.millisToRoundedTime(duration * 1000L)));

		meta.addCustomEffect(new PotionEffect(type.getEffectType(), duration * 20, level - 1), false);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);

		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static ItemStack buildSkull(String user) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
		head.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setOwner(user);
		head.setItemMeta(skullMeta);
		return head;
	}

	public static ItemStack buildSkull(String user, String displayName) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
		head.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setOwner(user);
		skullMeta.setDisplayName(CC.translate(displayName));
		head.setItemMeta(skullMeta);
		return head;
	}

	public static ItemStack buildSkull(String user, String displayName, List<String> lore) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
		head.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setOwner(user);
		skullMeta.setDisplayName(CC.translate(displayName));
		List<String> loreStrings = new ArrayList<>();
		for (String string : lore) {
			loreStrings.add(CC.translate(string));
		}
		skullMeta.setLore(loreStrings);
		head.setItemMeta(skullMeta);
		return head;
	}

	public static ItemStack buildSkull(String user, String displayName, String... lore) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
		head.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setOwner(user);
		skullMeta.setDisplayName(CC.translate(displayName));
		List<String> loreStrings = new ArrayList<>();
		for (String string : lore) {
			loreStrings.add(CC.translate(string));
		}
		skullMeta.setLore(loreStrings);
		head.setItemMeta(skullMeta);
		return head;
	}

	public static String formatMaterial(Material material) {
		String name = material.toString();
		name = name.replace('_', ' ');
		String result = "" + name.charAt(0);
		for (int i = 1; i < name.length(); i++) {
			if (name.charAt(i - 1) == ' ') {
				result += name.charAt(i);
			} else {
				result += Character.toLowerCase(name.charAt(i));
			}
		}
		return result;
	}

	public static Material getItemFromString(String item) {
		return Material.getMaterial(item);
	}

	public static boolean hasInventorySpace(Player player) {
		Inventory inventory = player.getInventory();
		int stacks = 0;
		for (int i = 0; i < inventory.getContents().length; i++) {
			if (inventory.getItem(i) != null) {
				if (inventory.getItem(i).getType() != Material.AIR) {
					stacks++;
				} else {
					return true;
				}
			}
		}
		return stacks >= 36;
	}

	public static ItemStack setMonsterEggType(ItemStack item, EntityType type) {
		net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound idTag = new NBTTagCompound();
		idTag.setString("id", type.getName());
		NBTTagCompound tag = stack.hasTag() ? stack.getTag() : new NBTTagCompound();
		tag.set("EntityTag", idTag);
		stack.setTag(tag);
		return CraftItemStack.asBukkitCopy(stack);
	}

	public static boolean hasItemInInventory(Player player, Material material, int amount) {
		int rest = amount;
		int counted = 0;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack == null || stack.getType() != material)
				continue;
			if (rest <= stack.getAmount()) {
				return true;
			} else if (rest > stack.getAmount()) {
				counted = counted + stack.getAmount();
				if (counted >= rest) {
					return true;
				}
			} else {
				break;
			}
		}
		return false;
	}

	public static boolean hasItemInInventory(Player player, Material material, int amount, short id) {
		int rest = amount;
		int counted = 0;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack == null || stack.getType() != material)
				continue;
			if (stack.getData().getData() != id)
				continue;
			if (rest <= stack.getAmount()) {
				return true;
			} else if (rest > stack.getAmount()) {
				counted = counted + stack.getAmount();
				if (counted >= rest) {
					return true;
				}
			} else {
				break;
			}
		}
		return false;
	}

	public static void removePlayerItems(Player player, Material material, short data, int amount) {
		int rest = amount;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);

			if (stack == null || stack.getType() != material)
				continue;
			if (stack.getData().getData() != data)
				continue;
			if (rest >= stack.getAmount()) {
				rest -= stack.getAmount();
				player.getInventory().clear(i);
			} else if (rest > 0) {
				stack.setAmount(stack.getAmount() - rest);
				rest = 0;
			} else {
				break;
			}
		}
	}

	public static void removePlayerItems(Player player, Material material, int amount) {
		int rest = amount;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);

			if (stack == null || stack.getType() != material)
				continue;
			if (rest >= stack.getAmount()) {
				rest -= stack.getAmount();
				player.getInventory().clear(i);
			} else if (rest > 0) {
				stack.setAmount(stack.getAmount() - rest);
				rest = 0;
			} else {
				break;
			}
		}
	}

}
