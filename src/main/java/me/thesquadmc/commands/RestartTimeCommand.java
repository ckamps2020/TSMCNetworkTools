package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.TimeUtils;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RestartTimeCommand implements CommandExecutor {

	private final Main main;

	public RestartTimeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			int i = 720 - main.getRestartTime();
			player.sendMessage(CC.translate("&e&lRESTART &6■ &7Server restarts in &e" + TimeUtils.convertPlaytime(i)));
		}
		return true;
	}

}
