package me.thesquadmc.utils.nms;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public final class AIUtils {

	public static void setAi(Entity entity, boolean enabled) {
		try {
			if (enabled) {
				net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
				NBTTagCompound tag = new NBTTagCompound();
				nmsEntity.c(tag);
				tag.setInt("NoAI", 0);
				EntityLiving el = (EntityLiving) nmsEntity;
				el.a(tag);
			} else {
				net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
				NBTTagCompound tag = new NBTTagCompound();
				nmsEntity.c(tag);
				tag.setInt("NoAI", 1);
				EntityLiving el = (EntityLiving) nmsEntity;
				el.a(tag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
