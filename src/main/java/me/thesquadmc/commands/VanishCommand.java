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

public final class VanishCommand implements CommandExecutor {

	private final Main main;

	public VanishCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.vanish")) {
				if (!tempData.isVanished()) {
					hidePlayerSpectator(player);
					tempData.setVanished(true);
					player.sendMessage(StringUtils.msg("&e&lVANISH &6■ &7You toggled vanish &eon&7! No one will be able to see you"));
				} else {
					showPlayerSpectator(player);
					tempData.setVanished(false);
					player.sendMessage(StringUtils.msg("&e&lVANISH &6■ &7You toggled vanish &eoff&7! No one will be able to see you"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

	private void hidePlayerSpectator(Player player) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
	}

	private void showPlayerSpectator(Player player) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
	}

}
