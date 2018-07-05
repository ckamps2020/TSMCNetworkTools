package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StaffAlertCommand implements CommandExecutor {

	private final Main main;

	public StaffAlertCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length >= 1) {
				String string = StringUtils.buildMessage(args, 0);
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
						player.sendMessage(CC.translate("&c&lALERT &c" + Unicode.SQUARE + " &7" + string));
					}
				}
			}
		} else {
			sender.sendMessage(CC.translate("&c&lALERT " + Unicode.SQUARE + " &7You are not allowed to use this in game!"));
		}
		return true;
	}

}
