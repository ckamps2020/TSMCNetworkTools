package me.thesquadmc.objects;

import me.thesquadmc.Main;
import me.thesquadmc.utils.nms.AIUtils;
import net.minecraft.server.v1_8_R3.PathEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class MobNPC {

	private String name;
	private String displayName;
	private EntityType entityType;
	private boolean ai;
	private Entity entity;
	private Hologram hologram;
	private CraftEntity craftEntity;

	public MobNPC(String name, String displayName, EntityType entityType, boolean ai, CraftEntity craftEntity) {
		this.name = name;
		this.displayName = displayName;
		this.entityType = entityType;
		this.ai = ai;
		this.craftEntity = craftEntity;
	}

	public void despawn() {
		this.entity.remove();
		Main.getMain().getHologramManager().getHolograms().get(name).despawn();
	}

	public void walk(Location location, Double speed, Entity entity) {
		PathEntity path = ((CraftCreature) entity)
				.getHandle().getNavigation().a(location.getX(), location.getY(), location.getZ());
		((CraftCreature) entity).getHandle().getNavigation().a(path, speed);
	}

	public void spawnEntity(Location location) {
		Entity entity = location.getWorld().spawnEntity(location, this.entityType);
		this.entity = entity;
		AIUtils.setAi(entity, this.ai);
		entity.setCustomNameVisible(false);
		Main.getMain().getHologramManager().createHologram(this.name, displayName, location);
		this.hologram = Main.getMain().getHologramManager().getHolograms().get(this.name);
	}

	public Hologram getHologram() {
		return hologram;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public boolean hasAi() {
		return ai;
	}

	public void setAi(boolean ai) {
		this.ai = ai;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
