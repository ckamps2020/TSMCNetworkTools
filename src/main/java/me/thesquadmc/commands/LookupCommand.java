package me.thesquadmc.commands;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LookupCommand implements CommandExecutor {

	private final Main main;

	public LookupCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (args.length == 1) {
					String name = args[0];
					User u = main.getLuckPermsApi().getUser(name);
					if (u != null) {
						UserData cachedData = u.getCachedData();
						Contexts contexts = Contexts.allowAll();
						MetaData metaData = cachedData.getMetaData(contexts);
						player.sendMessage(" ");
						player.sendMessage(StringUtils.msg("&6&l" + name));
						player.sendMessage(StringUtils.msg("&8■ &7Rank: &f" + metaData.getPrefix()));
						if (Bukkit.getServerName().toUpperCase().contains("SKYBLOCK") || Bukkit.getServerName().toUpperCase().contains("FACTIONS")) {
							player.sendMessage(StringUtils.msg("&8■ &7Has Flight: &f" + PlayerUtils.hasPermission(main.getLuckPermsApi().getGroup(u.getPrimaryGroup()), "essentials.fly")));
						}
					} else {
						player.sendMessage(StringUtils.msg("&e&lLOOKUP&6■ &7That player is not online"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /lookup <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
