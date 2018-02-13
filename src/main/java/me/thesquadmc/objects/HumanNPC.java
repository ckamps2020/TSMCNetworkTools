package me.thesquadmc.objects;

import com.mojang.authlib.GameProfile;
import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class HumanNPC {

	private Location location;
	private String displayName;
	private String playerSkin;
	private EntityPlayer entityPlayer;
	private int id;

	public HumanNPC(Location location, String displayName, String playerSkin) {
		this.location = location;
		this.displayName = displayName;
		this.playerSkin = playerSkin;
	}

	public void spawn(Player player) {
		try {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerSkin);
			MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
			WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
			GameProfile gameProfile = new GameProfile(offlinePlayer.getUniqueId(), displayName);
			EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
			entityPlayer = npc;
			id = npc.getId();
			npc.getProfile().getProperties().removeAll("textures");
			if (!playerSkin.equalsIgnoreCase("NONE")) {
				npc.getProfile().getProperties().put("textures", PlayerUtils.getSkinProperty(playerSkin));
			}
			Location loc = location;
			npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			PlayerConnection c = ((CraftPlayer) player).getHandle().playerConnection;
			c.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			c.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			Bukkit.getScheduler().runTaskLater(Main.getMain(), new Runnable() {
				@Override
				public void run() {
					c.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
				}
			}, 2 * 20L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy(Player player) {
		PlayerConnection c = ((CraftPlayer) player).getHandle().playerConnection;
		c.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
	}

	public int getId() {
		return id;
	}

	public Location getLocation() {
		return location;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPlayerSkin() {
		return playerSkin;
	}

	public EntityPlayer getEntityPlayer() {
		return entityPlayer;
	}

}
