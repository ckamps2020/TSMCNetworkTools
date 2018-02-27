package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.nms.TitleUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TitleCommand implements CommandExecutor {

	private final Main main;

	public TitleCommand(Main main) {
		this.main = main;
	}

	//title <msg>~<msg>

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length >= 1) {
				String string = StringUtils.buildMessage(args, 0);
				String regex = "[~]+";
				String[] tokens = string.split(regex);
				int i = 0;
				String title = null;
				String subtitle = null;
				for (String s : tokens) {
					if (i == 0) {
						title = s;
						i++;
					} else if (i == 1) {
						subtitle = s;
						i++;
					} else {
						break;
					}
				}
				if (title != null && subtitle != null) {
					sender.sendMessage(CC.translate("&e&lTITLE &6■ &7Title sent!"));
					TitleUtils.sendTitleToServer(title, subtitle, 20, 40, 20);
				} else {
					sender.sendMessage(CC.translate("&e&lTITLE &6■ &7Please supply a valid title and subtitle!"));
				}
			} else {
				sender.sendMessage(CC.translate("&e&lTITLE &6■ &7Usage: /title <msg>~<msg>"));
			}
		} else {
			sender.sendMessage(CC.translate("&e&lTITLE &6■ &7You can't use this in game at the moment!"));
		}
		return true;
	}

}
