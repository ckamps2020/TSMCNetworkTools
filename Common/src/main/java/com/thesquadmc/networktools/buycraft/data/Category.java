package com.thesquadmc.networktools.buycraft.data;

import com.thesquadmc.networktools.buycraft.Buycraft;

import java.util.HashSet;
import java.util.Set;

public final class Category {

    private int id;
    private int order;
    private String name;

    private boolean onlySubCategories;

    private Set<Category> subCategories;
    private Set<Package> packages;

    public Category(int id, int order, String name, Set<Category> subCategories) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.subCategories = subCategories;
        this.packages = new HashSet<>();
        this.onlySubCategories = true;
    }

    public Category(int id, String name, int order, Set<Package> packages) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.packages = packages;
        this.subCategories = new HashSet<>();
        onlySubCategories = false;
    }

    public Category(int id, int order, String name, Set<Category> subCategories, Set<Package> packages) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.subCategories = subCategories;
        this.packages = packages;
        this.onlySubCategories = !subCategories.isEmpty() && packages.isEmpty();
    }


    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public boolean isOnlySubCategories() {
        return onlySubCategories;
    }

    public Set<Category> getSubCategories() {
        return subCategories;
    }

    public Set<Package> getPackages() {
        return packages;
    }

    public Package getPackage(int id) {
        return Buycraft.filterAndGet(packages.stream(), pack -> id == pack.getId());
    }

    public Package getPackage(String name) {
        return Buycraft.filterAndGet(packages.stream(), pack -> name.equals(pack.getName()));
    }

    public Category getSubCategory(int id) {
        return Buycraft.filterAndGet(subCategories.stream(), category -> id == category.getId());
    }

    public Category getSubCategory(String name) {
        return Buycraft.filterAndGet(subCategories.stream(), category -> name.equals(category.getName()));
    }

}
