package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSilenceCommand implements CommandExecutor {

	private final Main main;

	public ChatSilenceCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (!main.isChatSilenced()) {
					main.setChatSilenced(true);
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(StringUtils.msg("&e&lCHAT &6■ &7Chat silence has been &eenabled!"));
					Bukkit.broadcastMessage(" ");
				} else {
					main.setChatSilenced(false);
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(StringUtils.msg("&e&lCHAT &6■ &7Chat silence has been &edisabled!"));
					Bukkit.broadcastMessage(" ");
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}