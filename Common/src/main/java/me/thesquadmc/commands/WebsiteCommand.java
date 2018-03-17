package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WebsiteCommand implements CommandExecutor {

	private final Main main;

	public WebsiteCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(CC.translate("&a&lWEBSITE &2■ &7www.thesquadmc.net"));
		}
		return true;
	}

}
