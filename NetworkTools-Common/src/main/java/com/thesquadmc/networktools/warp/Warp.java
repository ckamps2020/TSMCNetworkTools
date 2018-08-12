package com.thesquadmc.networktools.warp;

import org.bukkit.Location;

import java.util.Objects;

public class Warp {

    private String name;
    private Location location;

    public Warp() {
    } //For Gson

    public Warp(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warp warp = (Warp) o;
        return Objects.equals(name, warp.name) &&
                Objects.equals(location, warp.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }

    @Override
    public String toString() {
        return "Warp{" +
                "name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}