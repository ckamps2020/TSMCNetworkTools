package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class QueueRestartCommand implements CommandExecutor {

	private final Main main;

	public QueueRestartCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
				if (args.length == 1) {
					try {
						int i = Integer.valueOf(args[0]);
						main.setRestartTime(i);
						player.sendMessage(CC.translate("&e&lRESTART &6■ &7Restart time has been set to &e" + TimeUtils.convert(main.getRestartTime())));
					} catch (Exception e) {
						player.sendMessage(CC.translate("&e&lRESTART &6■ &e" + args[0] + " &7is not a valid number!"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lRESTART &6■ &7Usage: /queuerestart <time>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
