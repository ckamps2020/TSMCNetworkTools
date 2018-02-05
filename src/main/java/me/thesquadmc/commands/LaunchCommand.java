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
import org.bukkit.util.Vector;

public final class LaunchCommand implements CommandExecutor {

	private final Main main;

	public LaunchCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("all")) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.setVelocity(new Vector(0, 10, 0));
							p.sendMessage(StringUtils.msg("&c&lWHOOSH!"));
						}
					} else {
						Player t = Bukkit.getPlayer(args[0]);
						if (t != null) {
							player.sendMessage(StringUtils.msg("&cYou launched " + t.getName()));
							t.setVelocity(new Vector(0, 10, 0));
							t.sendMessage(StringUtils.msg("&c&lWHOOSH!"));
						} else {
							player.sendMessage(StringUtils.msg("&cThat player is offline or does not exist!"));
						}
					}
				} else {
					for (Player p : PlayerUtils.getNearbyPlayers(player.getLocation(), 300)) {
						p.setVelocity(new Vector(0, 10, 0));
						p.sendMessage(StringUtils.msg("&c&lWHOOSH!"));
					}
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
