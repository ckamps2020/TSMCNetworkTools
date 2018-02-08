package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.ServerUtils;
import me.thesquadmc.utils.StringUtils;
import me.thesquadmc.utils.enums.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {


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
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Free Memory: &e" + ServerUtils.getFreeMemory()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Used Memory: &e" + ServerUtils.getUsedMemory()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Total Memory: &e" + ServerUtils.getTotalMemory()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Used Memory: &e" + ServerUtils.getMemoryPercentageUsed()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Recent TPS: &e" + ServerUtils.getTPS(0)));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Active Thread Count: &e" + ServerUtils.getActiveThreadCount()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Thread Pool Size: &e" + ServerUtils.getThreadPoolSize()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Largest Pool Size: &e" + ServerUtils.getLargestPoolSize()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7Process CPU Load: &e" + ServerUtils.getProcessCpuLoadFormatted()));
				player.sendMessage(StringUtils.msg("&e&lSTATUS &6■ &7System CPU Load: &e" + ServerUtils.getSystemCpuLoadFormatted()));
				player.sendMessage(" ");
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return false;
	}

}
