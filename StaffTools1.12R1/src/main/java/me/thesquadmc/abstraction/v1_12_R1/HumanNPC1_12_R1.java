package me.thesquadmc.abstraction.v1_12_R1;

import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.HumanNPC;
import me.thesquadmc.abstraction.MojangGameProfile;
import me.thesquadmc.abstraction.NMSAbstract;

public class HumanNPC1_12_R1 implements HumanNPC {

	private int id;
	private EntityPlayer handle;

	private final Location location;
	private final String displayName, playerSkin;
	
	public HumanNPC1_12_R1(Location location, String displayName, String playerSkin) {
		this.location = location;
		this.displayName = displayName;
		this.playerSkin = playerSkin;
	}

	@Override
	public void spawn(Player... players) {
		@SuppressWarnings("deprecation") // Stupid Bukkit 1.8 -,-
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerSkin);
		NMSAbstract nmsAbstract = Main.getMain().getNMSAbstract();
		
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
			
			Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> 
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