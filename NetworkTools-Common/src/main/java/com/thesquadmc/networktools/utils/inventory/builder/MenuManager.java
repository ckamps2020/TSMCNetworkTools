package com.thesquadmc.networktools.utils.inventory.builder;

import com.google.common.collect.Sets;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.Set;

public class MenuManager {

    private final Set<Menu> menus = Sets.newHashSet();

    public MenuManager() {
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), NetworkTools.getInstance());
    }

    public void addMenu(Menu menu) {
        menus.add(menu);
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public Optional<Menu> getMenu(Inventory inventory) {
        for (Menu menu : menus) {

            Optional<Inventory> optInv = menu.getInventories().stream().filter(inv -> inventory.hashCode() == inv.hashCode()).findFirst();
            if (optInv.isPresent()) {
                return Optional.of(menu);
            }
        }

        return Optional.empty();
    }
}
