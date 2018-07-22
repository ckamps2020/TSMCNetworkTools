package com.thesquadmc.abstraction.v1_12_R1;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.abstraction.MobNPC;
import com.thesquadmc.managers.HologramManager;
import com.thesquadmc.objects.Hologram;
import com.thesquadmc.utils.nms.EntityUtils;
import net.minecraft.server.v1_12_R1.NavigationAbstract;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MobNPC1_12_R1 implements MobNPC {

    private String name, displayName;
    private EntityType type;
    private boolean ai;
    private Entity entity;
    private Hologram hologram;

    public MobNPC1_12_R1(String name, String displayName, EntityType type, boolean ai) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.ai = ai;
    }

    @Override
    public void spawn(Location location) {
        this.entity = location.getWorld().spawnEntity(location, type);
        entity.setCustomNameVisible(false);

        if (entity instanceof LivingEntity) {
            EntityUtils.setAI((LivingEntity) entity, false);
        }

        // TODO: This can be cleaned up
        HologramManager hologramManager = NetworkTools.getInstance().getHologramManager();
        hologramManager.createHologram(name, displayName, location);
        this.hologram = hologramManager.getHologram(name);
    }

    @Override
    public void destroy() {
        this.entity.remove();
        NetworkTools.getInstance().getHologramManager().getHologram(name).despawn();
    }

    @Override
    public void walk(Location location, double speed) {
        NavigationAbstract navigation = ((CraftCreature) entity).getHandle().getNavigation();
        navigation.a(location.getX(), location.getY(), location.getZ(), speed);
    }

    @Override
    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public EntityType getEntityType() {
        return null;
    }

    @Override
    public boolean hasAI() {
        return ai;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

}