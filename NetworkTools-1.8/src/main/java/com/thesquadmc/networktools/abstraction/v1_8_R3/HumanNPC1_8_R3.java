package com.thesquadmc.networktools.abstraction.v1_8_R3;

import com.mojang.authlib.GameProfile;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.HumanNPC;
import com.thesquadmc.networktools.abstraction.MojangGameProfile;
import com.thesquadmc.networktools.abstraction.NMSAbstract;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HumanNPC1_8_R3 implements HumanNPC {

	private int id;
	private EntityPlayer handle;

	private final Location location;
	private final String displayName, playerSkin;
	
	public HumanNPC1_8_R3(Location location, String displayName, String playerSkin) {
		this.location = location;
		this.displayName = displayName;
		this.playerSkin = playerSkin;
	}

	@Override
	public void spawn(Player... players) {
		@SuppressWarnings("deprecation") // Stupid Bukkit 1.8 -,-
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerSkin);
		NMSAbstract nmsAbstract = NetworkTools.getInstance().getNMSAbstract();
		
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
		GameProfile targetProfile = new GameProfile(offlinePlayer.getUniqueId(), displayName);
		
		this.handle = new EntityPlayer(server, world, targetProfile, new PlayerInteractManager(world));
		this.id = handle.getId();
		
		MojangGameProfile profile = nmsAbstract.getGameProfile((Player) handle);
		profile.removeProperty("textures");
		if (!playerSkin.equalsIgnoreCase("NONE")) {
			profile.addProperty("textures", nmsAbstract.getSkinProperty(playerSkin));
		}
		
		this.handle.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
		
		for (Player player : players) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, handle));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(handle));

			Bukkit.getScheduler().runTaskLater(NetworkTools.getInstance(), () ->
				connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, handle)),
			40L);
		}
	}

	@Override
	public void destroy(Player... players) {
		for (Player player : players) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutEntityDestroy(id));
		}
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getPlayerSkin() {
		return playerSkin;
	}

	@Override
	public Object getHandle() {
		return handle;
	}

}