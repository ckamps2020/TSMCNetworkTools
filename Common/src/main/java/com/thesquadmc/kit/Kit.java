package com.thesquadmc.kit;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kit {

    private String name;
    private List<ItemStack> items = new ArrayList<>();

    public Kit() {
    } //For Gson

    public Kit(String name, List<ItemStack> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(items);
    }
}