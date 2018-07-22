package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class NTVersionCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public NTVersionCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(CC.translate("&e&lBUILD &6■ &7Version &e" + networkTools.getVersion()));
		}
		return true;
	}

}
