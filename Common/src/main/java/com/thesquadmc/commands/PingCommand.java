package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PingCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public PingCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				String name = args[0];
				Player t = Bukkit.getPlayer(name);
				if (t != null) {
					int ping = networkTools.getNMSAbstract().getPing(player);
					player.sendMessage(CC.translate("&e&lPING &6■ &e" + t.getName() + "&7's ping is currently &e" + ping + "&7ms"));
				}
			} else {
				int ping = networkTools.getNMSAbstract().getPing(player);
				player.sendMessage(CC.translate("&e&lPING &6■ &7Your ping is currently &e" + ping + "&7ms"));
			}
		}
		return true;
	}

}
