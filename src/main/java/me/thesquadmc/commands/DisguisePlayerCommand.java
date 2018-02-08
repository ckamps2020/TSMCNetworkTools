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

public final class DisguisePlayerCommand implements CommandExecutor {

	private final Main main;

	public DisguisePlayerCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				if (args.length == 2) {
					String user = args[0];
					String name = args[1];
					if (user.equalsIgnoreCase("all")) {
						PlayerUtils.updateGlobalSkin(name);
						Bukkit.getScheduler().runTaskLater(main, new Runnable() {
							@Override
							public void run() {
								player.sendMessage(StringUtils.msg("&e&lDISGUISE &6■ &7You have disguised the entire server to &e" + name));
								for (Player p : Bukkit.getOnlinePlayers()) {
									PlayerUtils.setSameSkin(p);
								}
							}
						}, 10L);
					} else {
						Player t = Bukkit.getPlayer(user);
						if (t != null) {
							PlayerUtils.setSkin(t, name);
						} else {
							player.sendMessage(StringUtils.msg("&cThat player is offline or doesnt exist!"));
						}
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /disguiseplayer <player/all> <name>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
