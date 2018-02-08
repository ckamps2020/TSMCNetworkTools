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

import java.util.Random;

public final class UndisguisePlayerCommand implements CommandExecutor {

	private final Main main;

	public UndisguisePlayerCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				if (args.length == 1) {
					String user = args[0];
					if (user.equalsIgnoreCase("all")) {
						if (!main.getSig().equalsIgnoreCase("NONE")) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								Random random = new Random();
								int n = random.nextInt(20) + 1;
								Bukkit.getScheduler().runTaskLater(main, new Runnable() {
									@Override
									public void run() {
										PlayerUtils.restorePlayerTextures(p);
									}
								}, n);
							}
							main.setSig("NONE");
							main.setValue("NONE");
							player.sendMessage(StringUtils.msg("&e&lDISGUISE &6■ &7You have undisguised the server!"));
						} else {
							player.sendMessage(StringUtils.msg("&e&lDISGUISE &6■ &7The server is not disguised!"));
						}
					} else {
						Player t = Bukkit.getPlayer(user);
						if (t != null) {
							player.sendMessage(StringUtils.msg("&e&lDISGUISE &6■ &7You have undisguised &e" + t.getName()));
							PlayerUtils.restorePlayerTextures(t);
						} else {
							player.sendMessage(StringUtils.msg("&cThat player is offline or doesnt exist!"));
						}
					}
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
