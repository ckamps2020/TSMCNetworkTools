package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApplyCommand implements CommandExecutor {

	private final Main main;

	public ApplyCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(CC.translate("&e&lAPPLY &6■ &7You can apply for staff at &ehttps://thesquadmc.net/threads/updated-helper-application-format-details.7/"));
		}
		return true;
	}

}
