package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.TimeUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Active Thread Count: &e" + ServerUtils.getActiveThreadCount()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Thread Pool Size: &e" + ServerUtils.getThreadPoolSize()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Largest Pool Size: &e" + ServerUtils.getLargestPoolSize()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Process CPU Load: &e" + ServerUtils.getProcessCpuLoadFormatted()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7System CPU Load: &e" + ServerUtils.getSystemCpuLoadFormatted()));
				player.sendMessage(CC.translate("&e&lSTATUS &6■ &7Uptime: &e" + TimeUtils.millisToRoundedTime(System.currentTimeMillis() - main.getStartup())));
				player.sendMessage(" ");
			} else {
				player.sendMessage(CC.translate("&cYou do not have permission to use this command!"));
			}
		}
		return false;
	}

}
