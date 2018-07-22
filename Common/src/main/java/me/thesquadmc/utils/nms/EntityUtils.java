package me.thesquadmc.utils.nms;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.abstraction.NMSAbstract;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.concurrent.ThreadLocalRandom;

public final class EntityUtils {

    private static final Color[] COLOURS = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE};
	private static final NMSAbstract NMS_ABSTRACT = NetworkTools.getInstance().getNMSAbstract();

	public static void setLookDirection(Entity entity, float yaw, float pitch) {
		NMS_ABSTRACT.setLookDirection(entity, pitch, yaw);
	}

	public static void setHeadYaw(LivingEntity entity, float yaw) {
		NMS_ABSTRACT.setHeadYaw(entity, yaw);
	}

	public static void launchRandomFirework(Location location, boolean instant) {
		Firework firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.values()[ThreadLocalRandom.current().nextInt(FireworkEffect.Type.values().length)])
                .withColor(COLOURS[ThreadLocalRandom.current().nextInt(COLOURS.length)])
                .trail(false)
                .build());

		fireworkMeta.setPower(1);
		firework.setFireworkMeta(fireworkMeta);

        if (instant) {
			firework.detonate();
		}
	}

	public static void setAI(LivingEntity entity, boolean enabled) {
		NetworkTools.getInstance().getNMSAbstract().setAI(entity, enabled);
	}

}
