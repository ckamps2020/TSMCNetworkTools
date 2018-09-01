package com.thesquadmc.networktools.utils.inventory.builder;

import com.google.common.collect.Maps;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import sun.nio.ch.Net;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class Menu implements Listener {

    private static final Consumer<Player> BLANK_CONSUMER = player -> { };

    /**
     * Name of the inventory. This will be shown at the top of the menu.
     */
    private String name;

    /**
     * Number of the inventory slots
     */
    private final int size;

    /**
     * Map of items that will be in the inventory
     */
    private final Map<Integer, MenuItem> items = Maps.newHashMap();

    /**
     * Bukkit inventories of players viewing this menu
     */
    private final Map<UUID, Inventory> inventories = Maps.newHashMap();

    /**
     * Consumer that is accepted when an inventory is opened
     */
    private Consumer<Player> onOpen = BLANK_CONSUMER;

    /**
     * Consumer that is accepted when an inventory is closed
     */
    private Consumer<Player> onClose = BLANK_CONSUMER;

    public Menu(String name, int size) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.size = size;

        NetworkTools.getInstance().getMenuManager().addMenu(this);
    }
    /**
     * Creates an inventory for the player
     * Only to be used for the #open method
     * @param player player to create the inventory for
     */
    public void build(Player player) {
        if (player != null && player.isOnline()) {
            Inventory inv = inventories.get(player.getUniqueId());

            if (inv != null) {
                player.openInventory(inv);
                return;
            }

            Inventory inventory = inventories.computeIfAbsent(player.getUniqueId(), uuid -> Bukkit.createInventory(null, size, name));
            inventory.clear();

            items.forEach((integer, menuItem) -> inventory.setItem(integer, menuItem.getItem(player)));

            inventories.put(player.getUniqueId(), inventory);
            player.openInventory(inventory);

            onOpen.accept(player);
        }
    }

    /**
     * Updates the inventory by closing and
     * opening it back up again
     */
    public void update() {
        inventories.forEach((uuid, inventory) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                inventories.remove(uuid);
                return;
            }

            if (!inventory.getViewers().contains(player)) {
                inventories.remove(uuid);
                return;
            }

            player.closeInventory();
            build(player);
        });
    }

    /**
     * Add a MenuItem to the next avaiable slot
     * @param menuItem
     */
    public void addMenuItem(MenuItem menuItem) {
        for (int i = 0; i <= items.size(); i++) {
            if (getMenuItem(i) == null) {
                addMenuItem(i, menuItem);
                return;
            }
        }
    }

    public void addMenuItem(int slot, MenuItem menuItem) {
        items.put(slot, menuItem);
        update();
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuItem getMenuItem(int slot) {
        return items.get(slot);
    }

    public Collection<Inventory> getInventories() {
        return inventories.values();
    }

    public Consumer<Player> getOnOpen() {
        return onOpen;
    }

    public void setOnOpen(Consumer<Player> onOpen) {
        this.onOpen = onOpen;
    }

    public Consumer<Player> getOnClose() {
        return onClose;
    }

    public void setOnClose(Consumer<Player> onClose) {
        this.onClose = onClose;
    }
}
