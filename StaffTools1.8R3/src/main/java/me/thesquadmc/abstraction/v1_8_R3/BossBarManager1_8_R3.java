package me.thesquadmc.abstraction.v1_8_R3;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

import me.thesquadmc.abstraction.BossBarManager;

public class BossBarManager1_8_R3 implements BossBarManager {
	
	private final Map<UUID, Integer> dragons = new HashMap<>();

	@Override
	public void setBar(Player player, String text, float healthPercent) {
		Location location = player.getLocation();
		WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
		
		EntityEnderDragon dragon = new EntityEnderDragon(world);
		dragon.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
		
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);
		DataWatcher watcher = new DataWatcher(null);
		
		watcher.a(0, 0x20);
		watcher.a(6, (healthPercent * 200) / 100);
		watcher.a(10, text);
		watcher.a(2, text);
		watcher.a(11, (byte) 1);
		watcher.a(3, (byte) 1);
		
		try {
			Field fieldT = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
			fieldT.setAccessible(true);
			fieldT.set(packet, watcher);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		this.dragons.put(player.getUniqueId(), dragon.getId());
	}

	@Override
	public void removeBar(Player player) {
		UUID playerUUID = player.getUniqueId();
		if (!dragons.containsKey(playerUUID)) return;
		
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(dragons.get(playerUUID));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		this.dragons.remove(playerUUID);
	}

	@Override
	public boolean hasBar(Player player) {
		return dragons.containsKey(player.getUniqueId());
	}

	@Override
	public void teleportBar(Player player) {
		if (!dragons.containsKey(player.getUniqueId())) return;
		
		Location loc = player.getLocation();
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(dragons.get(player.getUniqueId()),
				(int) loc.getX() * 32, (int) (loc.getY() - 100) * 32, (int) loc.getZ() * 32,
				(byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360), false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public void updateText(Player player, String text) {
		this.updateBar(player, text, -1);
	}

	@Override
	public void updateHealth(Player player, float healthPercent) {
		this.updateBar(player, null, healthPercent);
	}

	@Override
	public void updateBar(Player player, String text, float healthPercent) {
		if (!dragons.containsKey(player.getUniqueId())) return;
		
		DataWatcher watcher = new DataWatcher(null);
		watcher.a(0, 0x20);
		watcher.a(11, (byte) 1);
		watcher.a(3, (byte) 1);
		
		if (healthPercent != -1) {
			watcher.a(6, (healthPercent * 200) / 100);
		}
		if (text != null) {
			watcher.a(10, text);
			watcher.a(2, text);
		}
		
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(dragons.get(player.getUniqueId()), watcher, true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public Set<Player> getPlayers() {
		return dragons.keySet().stream()
				.map(Bukkit::getPlayer)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
}