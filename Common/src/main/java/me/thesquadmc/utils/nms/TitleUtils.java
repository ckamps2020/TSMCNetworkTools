package me.thesquadmc.utils.nms;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.abstraction.NMSAbstract;
import org.bukkit.entity.Player;

public final class TitleUtils {

	private static final NMSAbstract NMS_ABSTRACT = NetworkTools.getInstance().getNMSAbstract();

	public static void sendTitleToPlayer(String title, String subtitle, int in, int stay, int out, Player player) {
		NMS_ABSTRACT.sendTitle(player, title, subtitle, in, stay, out);
	}

	public static void sendTitleToServer(String title, String subtitle, int in, int stay, int out) {
		NMS_ABSTRACT.broadcastTitle(title, subtitle, in, stay, out);
	}

	public static void sendActionBarToPlayer(String actionBar, Player player) {
		NMS_ABSTRACT.sendActionBar(player, actionBar);
	}

	public static void sendActionBarToServer(String actionBar) {
		NMS_ABSTRACT.broadcastActionBar(actionBar);
	}

	public static void sendTablist(Player player, String headerMessage, String footerMessagae) {
		NMS_ABSTRACT.sendTabList(player, headerMessage, footerMessagae);
	}

	public static void sendTabListToServer(String headerMessage, String footerMessage) {
		NMS_ABSTRACT.broadcastTabList(headerMessage, footerMessage);
	}

}
