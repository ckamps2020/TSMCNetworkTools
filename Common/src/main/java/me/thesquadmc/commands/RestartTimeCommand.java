package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RestartTimeCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public RestartTimeCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			int i = 720 - networkTools.getRestartTime();
			player.sendMessage(CC.translate("&e&lRESTART &6■ &7Server restarts in &e" + TimeUtils.convertPlaytime(i)));
		}
		return true;
	}

}
