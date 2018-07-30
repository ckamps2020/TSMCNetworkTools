package me.thesquadmc.managers;

import me.thesquadmc.objects.Hologram;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public final class HologramManager {

	private Map<String, Hologram> holograms = new HashMap<>();

	public void registerHologram(String name, Hologram hologram) {
		holograms.put(name, hologram);
	}

	public void unregisterHologram(String name) {
		holograms.remove(name);
	}

	public boolean isHologramNameTaken(String name) {
		return holograms.containsKey(name);
	}

	public Hologram getHologram(String name) {
		return holograms.get(name);
	}

	public Map<String, Hologram> getHolograms() {
		return holograms;
	}

	public void createHologram(String name, String text, Location location) {
		Hologram hologram = new Hologram(name, text, location);
		hologram.spawn();
		holograms.put(name, hologram);
	}

}
