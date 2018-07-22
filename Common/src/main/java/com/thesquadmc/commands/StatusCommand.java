package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import com.thesquadmc.utils.server.ServerUtils;
import com.thesquadmc.utils.time.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public final class StatusCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public StatusCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
				player.sendMessage(" ");
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Free Memory: &e" + ServerUtils.getFreeMemory()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Used Memory: &e" + ServerUtils.getUsedMemory()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Total Memory: &e" + ServerUtils.getTotalMemory()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Used Memory: &e" + ServerUtils.getMemoryPercentageUsed()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Recent TPS: &e" + ServerUtils.getTPS(0)));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Process CPU Load: &e" + ServerUtils.getProcessCpuLoadFormatted()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7System CPU Load: &e" + ServerUtils.getSystemCpuLoadFormatted()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Available Logical Processors: &e" + Runtime.getRuntime().availableProcessors()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Available Hard Drive Space: &e" + Math.round(new File("/").getTotalSpace() / (1024.0 * 1024.0 * 1024.0)) + "GB"));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Request Was Served On Thread: &e" + Thread.currentThread().getName()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Uptime: &e" + TimeUtils.millisToRoundedTime(System.currentTimeMillis() - networkTools.getStartup())));
				player.sendMessage(" ");
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return false;
	}

}
