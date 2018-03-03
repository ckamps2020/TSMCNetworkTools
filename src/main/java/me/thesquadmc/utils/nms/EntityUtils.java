package me.thesquadmc.utils.nms;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public final class EntityUtils {

	public static void setLookDirection(net.minecraft.server.v1_8_R3.Entity entity, float yaw, float pitch) {
		if(entity == null) {
			return;
		}
		yaw = clampYaw(yaw);
		entity.yaw = yaw;
		setHeadYaw(entity, yaw);
		entity.pitch = pitch;
	}

	private static void setHeadYaw(net.minecraft.server.v1_8_R3.Entity en, float yaw) {
		if(!(en instanceof EntityLiving)) {
			return;
		}
		EntityLiving handle = (EntityLiving)en;
		yaw = clampYaw(yaw);
		handle.aK = yaw;
		if(!(handle instanceof EntityHuman)) {
			handle.aI = yaw;
		}
		handle.aL = yaw;
	}

	private static float clampYaw(float yaw) {
		while(yaw < -180.0F) {
			yaw += 360.0F;
		}
		while(yaw >= 180.0F) {
			yaw -= 360.0F;
		}
		return yaw;
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
			detonate(firework);
		}

	}

	public static void detonate(Firework firework) {
		((CraftFirework)firework).getHandle().expectedLifespan = 1;
	}

}
