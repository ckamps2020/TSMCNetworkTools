package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public final class StatusCommand implements CommandExecutor {

	private final Main main;

	public StatusCommand(Main main) {
		this.main = main;
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
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Uptime: &e" + TimeUtils.millisToRoundedTime(System.currentTimeMillis() - main.getStartup())));
				player.sendMessage(" ");
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return false;
	}

}
