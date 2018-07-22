package com.thesquadmc.utils.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(Material mat) {
        this.itemStack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, int data) {
        this.itemStack = new ItemStack(mat, 1, (short) data);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        for (String s : name) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = this.itemStack.getItemMeta();

        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }

        meta.setLore(toSet);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.itemStack.setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder data(int data) {
        this.itemStack.setData(new MaterialData(this.itemStack.getType(), (byte) data));
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        this.itemStack.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(new ArrayList<String>());
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment e : this.itemStack.getEnchantments().keySet()) {
            this.itemStack.removeEnchantment(e);
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (this.itemStack.getType() == Material.LEATHER_BOOTS ||
                this.itemStack.getType() == Material.LEATHER_CHESTPLATE ||
                this.itemStack.getType() == Material.LEATHER_HELMET
                || this.itemStack.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.itemStack.getItemMeta();
            meta.setColor(color);
            this.itemStack.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flags);
        itemStack.setItemMeta(meta);
        return this;
    }

    public <T extends ItemMeta> ItemBuilder customMeta(Class<T> metaType, Consumer<T> metaFunction) {
        if (!metaType.isInstance(metaType)) return this;
        ItemMeta meta = itemStack.getItemMeta();
        metaFunction.accept(metaType.cast(meta));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

}
