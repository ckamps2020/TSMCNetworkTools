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

public final class SmiteCommand implements CommandExecutor {

	private final Main main;

	public SmiteCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				if (args.length == 1) {
					String first = args[0];
					if (first.equalsIgnoreCase("all")) {
						for (Player t : Bukkit.getOnlinePlayers()) {
							t.getLocation().getWorld().strikeLightning(t.getLocation());
						}
						player.sendMessage(StringUtils.msg("&aYou stuck everyone with lightning!"));
					} else {
						Player t = Bukkit.getPlayer(first);
						if (t != null) {
							t.getLocation().getWorld().strikeLightning(t.getLocation());
							player.sendMessage(StringUtils.msg("&aYou stuck " + t.getName() + " with lightning!"));
						}
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /smite <player/all>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
