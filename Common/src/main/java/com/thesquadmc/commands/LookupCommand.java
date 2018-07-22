package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LookupCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public LookupCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (args.length == 1) {
					String name = args[0];
					User u = networkTools.getLuckPermsApi().getUser(name);
					if (u != null) {
						UserData cachedData = u.getCachedData();
						Contexts contexts = Contexts.allowAll();
						MetaData metaData = cachedData.getMetaData(contexts);
						player.sendMessage(" ");
						player.sendMessage(CC.translate("&6&l" + name));
						player.sendMessage(CC.translate("&8■ &7Rank: &f" + metaData.getPrefix()));
						if (Bukkit.getServerName().toUpperCase().contains("SKYBLOCK") || Bukkit.getServerName().toUpperCase().contains("FACTIONS") || Bukkit.getServerName().toUpperCase().contains("PRISON")) {
							player.sendMessage(CC.translate("&8■ &7Has Flight: &f" + PlayerUtils.hasPermission(networkTools.getLuckPermsApi().getGroup(u.getPrimaryGroup()), "essentials.fly")));
						}
					} else {
						player.sendMessage(CC.translate("&e&lLOOKUP&6■ &7That player is not online"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lLOOKUP &6■ &7Usage: /lookup <player>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
