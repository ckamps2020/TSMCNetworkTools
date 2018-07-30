package me.thesquadmc.utils.nms;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.NMSAbstract;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public final class EntityUtils {
	
	private static final NMSAbstract NMS_ABSTRACT = Main.getMain().getNMSAbstract();

	public static void setLookDirection(Entity entity, float yaw, float pitch) {
		NMS_ABSTRACT.setLookDirection(entity, pitch, yaw);
	}

	public static void setHeadYaw(LivingEntity entity, float yaw) {
		NMS_ABSTRACT.setHeadYaw(entity, yaw);
	}

	public static void launchRandomFirework(Location location, boolean instant) {
		Firework firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		Random random = new Random();
		Color[] colours = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE};
		fireworkMeta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]).withColor(colours[random.nextInt(colours.length)]).trail(false).build());
		fireworkMeta.setPower(1);
		firework.setFireworkMeta(fireworkMeta);
		if (instant) {
			firework.detonate();
		}
	}

	public static void setAI(LivingEntity entity, boolean enabled) {
		Main.getMain().getNMSAbstract().setAI(entity, enabled);
	}

}
