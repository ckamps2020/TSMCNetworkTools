package me.thesquadmc.objects;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public final class Hologram {

	private String name;
	private String text;
	private Location location;
	private Entity entity;
	private List<String> texts;
	private List<ArmorStand> armorStands;
	private boolean up;

	public Hologram(String name, Location location, boolean up, String... text) {
		for (String t : text) {
			texts.add(t);
		}
		this.name = name;
		this.location = location;
		this.up = up;
	}

	public Hologram(String name, String text, Location location) {
		this.name = name;
		this.text = text;
		this.location = location;
	}

	public List<String> getTexts() {
		return texts;
	}

	public List<ArmorStand> getArmorStands() {
		return armorStands;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Entity getEntity() {
		return entity;
	}

	public void despawn() {
		entity.remove();
	}

	public void unregisterMulti() {
		for (int i = 0; i < texts.size(); i++) {
			Main.getMain().getHologramManager().unregisterHologram(name + texts.get(i));
		}
	}

	public void despawnMulti() {
		for (ArmorStand armorStand : armorStands) {
			armorStand.remove();
		}
	}

	public void spawnRegisterMulti() {
		Location location = new Location(this.location.getWorld(), this.location.getX(), this.location.getY(), this.location.getZ());
		for (int i = 0; i < texts.size(); i++) {
			Entity entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
			this.entity = entity;
			ArmorStand armorStand = (ArmorStand) entity;
			armorStand.setVisible(false);
			armorStand.setGravity(false);
			armorStand.setCustomName(CC.translate(texts.get(i)));
			armorStand.setCustomNameVisible(true);
			if (up) {
				location.add(0, 0.3, 0);
			} else {
				location.subtract(0, 0.3, 0);
			}
			Main.getMain().getHologramManager().registerHologram(this.name + "-" + texts.get(i), this);
		}
	}

	public void spawn() {
		Location location = this.location;
		Entity entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		this.entity = entity;
		ArmorStand armorStand = (ArmorStand) entity;
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setCustomName(CC.translate(text));
		armorStand.setCustomNameVisible(true);
	}

	public void updateText(String message) {
		ArmorStand armorStand = (ArmorStand) entity;
		text = message;
		armorStand.setCustomName(CC.translate(message));
	}

}
