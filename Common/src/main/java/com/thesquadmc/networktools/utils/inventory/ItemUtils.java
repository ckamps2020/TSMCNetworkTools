package com.thesquadmc.networktools.utils.inventory;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.NMSAbstract;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

    private static final NMSAbstract NMS_ABSTRACT = NetworkTools.getInstance().getNMSAbstract();
    private static final int[] REMOVE_ORDER = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 8, 7, 6, 5, 4, 3, 2, 1, 0};

    public static void spawnItem(ItemStack itemStack, Location location) {
        location.getWorld().dropItemNaturally(location, itemStack);
    }

    public static void dropInventory(Player player) {
        for (ItemStack s : player.getInventory().getContents()) {
            if (s != null && s.getType() != Material.AIR) {
                Bukkit.getWorld(player.getWorld().getName()).dropItemNaturally(player.getLocation(), s);
            }
        }
    }

    public static String toBase64(ItemStack item) {
        return NMS_ABSTRACT.toBase64(item);
    }

    public static ItemStack fromBase64(String data) {
        return NMS_ABSTRACT.fromBase64(data);
    }

    public static ItemStack createPotion(String name, PotionType type, int level, int duration) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }

        meta.setLore(Arrays.asList(
                "", CC.GRAY + StringUtils.toNiceString(type.name()) + " " + level + " Potion",
                CC.GRAY + "    Duration: " + TimeUtils.getFormattedTime(duration * 1000L)));

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
        return stacks < 36;
    }

    public static ItemStack setMonsterEggType(ItemStack item, EntityType type) {
        return NMS_ABSTRACT.setMonsterEggType(item, type);
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

    public static boolean removePlayerItems(Player p, ItemStack itemStack) {
        return removePlayerItems(p, itemStack, 1);
    }

    public static boolean removePlayerItems(Player player, ItemStack itemStack, int amount) {
        Inventory inv = player.getInventory();
        itemStack = itemStack.clone();
        for (int i : REMOVE_ORDER) {
            ItemStack cur = inv.getItem(i);
            if (cur != null && cur.isSimilar(itemStack)) {
                if (cur.getAmount() >= amount) {
                    cur.setAmount(cur.getAmount() - amount);
                    if (cur.getAmount() <= 0) {
                        cur = null;
                    }
                    inv.setItem(i, cur);
                    player.updateInventory();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Gets a number of empty slots there are in an
     * inventory
     *
     * @param inventory the inventory to check
     * @return an integer with the amount of empty slots
     */
    public static int getEmptySlots(Inventory inventory) {
        int empty = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                empty++;
            }
        }

        return empty;
    }

}
