package me.thesquadmc.objects;

import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class Hologram {

	private String name;
	private String text;
	private Location location;
	private Entity entity;

	public Hologram(String name, String text, Location location) {
		this.name = name;
		this.text = text;
		this.location = location;
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
