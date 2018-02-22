package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class UniquePlayersCommand implements CommandExecutor {

	private final Main main;

	public UniquePlayersCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
				Multithreading.runAsync(new Runnable() {
					@Override
					public void run() {
						try {
							int i = main.getMySQL().getTotalUniqueAccounts();
							player.sendMessage(CC.translate("&e&lACCOUNTS &6■ &7Total unique accounts: &e" + i));
						} catch (Exception e) {
							player.sendMessage(CC.translate("&e&lACCOUNTS &6■ &7Unable to fetch unique account count right now"));
							e.printStackTrace();
						}
					}
				});
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
