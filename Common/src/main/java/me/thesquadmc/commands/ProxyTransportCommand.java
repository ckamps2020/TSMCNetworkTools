package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import me.thesquadmc.utils.server.ConnectionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ProxyTransportCommand implements CommandExecutor {

	private final Main main;

	public ProxyTransportCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				if (args.length == 1) {
					String server = args[0];
					ConnectionUtils.sendPlayer(player, server);
				} else {
					player.sendMessage(CC.translate("&e&lTRANSPORT &6■ &7Usage: /proxytransport <server>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
