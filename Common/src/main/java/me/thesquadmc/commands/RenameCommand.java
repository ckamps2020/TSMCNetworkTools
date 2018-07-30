package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RenameCommand implements CommandExecutor {

	private final Main main;

	public RenameCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
				if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
					if (args.length >= 1) {
						String name = StringUtils.buildMessage(args, 0);
						player.getItemInHand().getItemMeta().setDisplayName(CC.translate(name));
						player.sendMessage(CC.translate("&e&lRENAME &6■ &7You renamed your &e" + player.getItemInHand().getType().name() + " &7to " + name));
					} else {
						player.sendMessage(CC.translate("&e&lRENAME &6■ &7You must provide a new name!"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lRENAME &6■ &7You do not have an item in your hand!"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
