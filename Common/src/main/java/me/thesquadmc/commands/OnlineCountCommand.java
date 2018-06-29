package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class OnlineCountCommand implements CommandExecutor {

	private final Main main;

	public OnlineCountCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				player.sendMessage(CC.translate("&7"));
				player.sendMessage(CC.translate("&c&lFACTIONS &c■ &7" + main.getCountManager().getFactionsCount()));
				player.sendMessage(CC.translate("&9&lCREATIVE &9■ &7" + main.getCountManager().getCreativeCount()));
				player.sendMessage(CC.translate("&d&lSKYBLOCK &d■ &7" + main.getCountManager().getSkyblockCount()));
				player.sendMessage(CC.translate("&b&lPRISON &b■ &7" + main.getCountManager().getPrisonCount()));
				player.sendMessage(CC.translate("&6&lHUB &6■ &7" + main.getCountManager().getHubCount()));
				player.sendMessage(CC.translate("&7"));
				player.sendMessage(CC.translate("&e" + main.getCountManager().getTotalOnlineCount() + "&8/&e4000"));
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
