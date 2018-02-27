package me.thesquadmc.utils.nms;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;

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

}
