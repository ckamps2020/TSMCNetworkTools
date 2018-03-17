package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishListCommand implements CommandExecutor {

	private final Main main;

	public VanishListCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				StringBuilder stringBuilder = new StringBuilder();
				for (Player p : Bukkit.getOnlinePlayers()) {
					TempData tempData = main.getTempDataManager().getTempData(p.getUniqueId());
					if (tempData.isYtVanishEnabled()) {
						stringBuilder.append(p.getName() + " ");
					}
				}
				StringBuilder sb = new StringBuilder();
				for (Player p : Bukkit.getOnlinePlayers()) {
					TempData tempData = main.getTempDataManager().getTempData(p.getUniqueId());
					if (tempData.isVanished()) {
						sb.append(p.getName() + " ");
					}
				}
				player.sendMessage(CC.translate("&e&lVANISH &6■ &7Listing all users in vanish..."));
				player.sendMessage(" ");
				player.sendMessage(CC.translate("&6■ &7YT Vanish: " + stringBuilder.toString()));
				player.sendMessage(CC.translate("&6■ &7Normal Vanish: " + sb.toString()));
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
