package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class UnFreezeCommand implements CommandExecutor {


	private final Main main;

	public UnFreezeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (args.length == 1) {
					String name = args[0];
					Player t = Bukkit.getPlayer(name);
					if (t != null) {
						if (!PlayerUtils.isEqualOrHigherThen(t, Rank.MOD)) {
							if (FreezeCommand.getFrozen().contains(t.getUniqueId())) {
								PlayerUtils.unfreezePlayer(t);
								main.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
								main.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
								main.getFrozenInventory().getAdmitted().remove(player.getUniqueId());
								main.getFrozenInventory().getDenying().remove(player.getUniqueId());
								main.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
								FreezeCommand.getFrozen().remove(t.getUniqueId());
								t.closeInventory();
								t.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &7You have been &eunfrozen&7. Thank you for your &epatience&7"));
								player.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &7You have unfrozen &e" + t.getName() + "&7!"));
							} else {
								player.sendMessage(StringUtils.msg("&cThat player is not frozen!"));
							}
						} else {
							player.sendMessage(StringUtils.msg("&cYou are not allowed to freeze another staff member!"));
						}
					} else {
						player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /freeze <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
