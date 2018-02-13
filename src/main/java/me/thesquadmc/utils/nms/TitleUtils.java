package me.thesquadmc.utils.nms;

import me.thesquadmc.utils.msgs.CC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public final class TitleUtils {

	public static void sendTitleToPlayer(String title, String subtitle, int in, int stay, int out, Player player) {
		Packet<?> titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(title)));
		Packet<?> subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(subtitle)));
		Packet<?> timingsPacket = new PacketPlayOutTitle(in, stay, out);
		CraftPlayer craftPlayer = (CraftPlayer) player;
		PlayerConnection connection = craftPlayer.getHandle().playerConnection;
		connection.sendPacket(titlePacket);
		connection.sendPacket(subtitlePacket);
		connection.sendPacket(timingsPacket);
	}

	public static void sendTitleToServer(String title, String subtitle, int in, int stay, int out) {
		Packet<?> titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(title)));
		Packet<?> subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}").a(CC.translate(subtitle)));
		Packet<?> timingsPacket = new PacketPlayOutTitle(in, stay, out);
		Bukkit.getOnlinePlayers().forEach(player -> {
			CraftPlayer craftPlayer = (CraftPlayer) player;
			PlayerConnection connection = craftPlayer.getHandle().playerConnection;
			connection.sendPacket(titlePacket);
			connection.sendPacket(subtitlePacket);
			connection.sendPacket(timingsPacket);
		});
	}

	public static void sendActionBarToServer(String actionBar) {
		Packet<?> actionBarPacket = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(actionBar) + "\"}"), (byte)2);
		Bukkit.getOnlinePlayers().forEach(player -> {
			CraftPlayer craftPlayer = (CraftPlayer) player;
			PlayerConnection connection = craftPlayer.getHandle().playerConnection;
			connection.sendPacket(actionBarPacket);
		});
	}

	public static void sendActionBarToPlayer(String actionBar, Player player) {
		Packet<?> actionBarPacket = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(actionBar) + "\"}"), (byte)2);
		CraftPlayer craftPlayer = (CraftPlayer) player;
		PlayerConnection connection = craftPlayer.getHandle().playerConnection;
		connection.sendPacket(actionBarPacket);
	}

	public static void sendTablist(Player player, String headerMessage, String footerMessagae) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		PlayerConnection connection = craftPlayer.getHandle().playerConnection;
		IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(headerMessage) +"\"}");
		IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(footerMessagae) + "\"}");
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		try {
			Field headerField = packet.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(packet, header);
			headerField.setAccessible(!headerField.isAccessible());

			Field footerField = packet.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(packet, footer);
			footerField.setAccessible(!footerField.isAccessible());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		connection.sendPacket(packet);
	}

}
