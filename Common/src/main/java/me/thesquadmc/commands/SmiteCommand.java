package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
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
				if (args.length == 0) {
					player.getWorld().strikeLightning(PlayerUtils.getTargetBlock(player, 10).getLocation());
					player.sendMessage(CC.translate("&e&lSMITE &6■ &c&lKABOOM!"));
				} else if (args.length == 1) {
					String first = args[0];
					if (first.equalsIgnoreCase("all")) {
						for (Player t : Bukkit.getOnlinePlayers()) {
							t.getLocation().getWorld().strikeLightning(t.getLocation());
						}
						player.sendMessage(CC.translate("&e&lSMITE &6■ &7You stuck everyone with lightning!"));
					} else {
						Player t = Bukkit.getPlayer(first);
						if (t != null) {
							t.getLocation().getWorld().strikeLightning(t.getLocation());
							player.sendMessage(CC.translate("&e&lSMITE &6■ &7You stuck " + t.getName() + " with lightning!"));
						}
					}
				} else {
					player.sendMessage(CC.translate("&e&lSMITE &6■ &7Usage: /smite (player/all)"));
				}
			} else {
				player.sendMessage(CC.translate("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
