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

import java.util.Random;

public final class RandomTPCommand implements CommandExecutor {

	private final Main main;

	public RandomTPCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.randomtp")) {
				if (Bukkit.getOnlinePlayers().size() > 10) {
					int random = new Random().nextInt(Bukkit.getServer().getOnlinePlayers().size());
					Player t = (Player) Bukkit.getServer().getOnlinePlayers().toArray()[random];
					player.teleport(t.getLocation());
					player.sendMessage(StringUtils.msg("&e&lSTAFF &6■ &7You have been randomly teleported to &e" + t.getName() + "&7"));
				} else {
					player.sendMessage(StringUtils.msg("&e&lSTAFF &6■ &7There are &enot enough &7players online to do this"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
