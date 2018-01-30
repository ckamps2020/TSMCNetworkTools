package me.thesquadmc.commands;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public final class UnFreezeCommand implements CommandExecutor {


	private final Main main;

	public UnFreezeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.freeze")) {
				if (args.length == 1) {
					String name = args[0];
					Player t = Bukkit.getPlayer(name);
					if (t != null) {
						User u = main.getLuckPermsApi().getUser(t.getUniqueId());
						if (!main.hasPerm(u, "tools.staff.freeze")) {
							if (FreezeCommand.getFrozen().contains(t.getUniqueId())) {
								unfreezePlayer(t);
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

	public static void unfreezePlayer(Player player) {
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		player.removePotionEffect(PotionEffectType.JUMP);
	}

}
