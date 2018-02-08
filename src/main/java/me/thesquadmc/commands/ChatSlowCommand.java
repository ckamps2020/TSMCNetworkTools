package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ChatSlowCommand implements CommandExecutor {

	private final Main main;

	public ChatSlowCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (args.length == 1) {
					try {
						int i = Integer.valueOf(args[0]);
						if (i == 0) {
							Bukkit.broadcastMessage(" ");
							Bukkit.broadcastMessage(StringUtils.msg("&e&lCHAT &6■ &7Chat delay has been disabled"));
							Bukkit.broadcastMessage(" ");
						} else {
							Bukkit.broadcastMessage(" ");
							Bukkit.broadcastMessage(StringUtils.msg("&e&lCHAT &6■ &7Chat delay set to &e" + i));
							Bukkit.broadcastMessage(" ");
						}
						main.setChatslow(i);
					} catch (Exception e) {
						player.sendMessage(StringUtils.msg("&cThat is not a valid number!"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /chatslow <time in seconds>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
