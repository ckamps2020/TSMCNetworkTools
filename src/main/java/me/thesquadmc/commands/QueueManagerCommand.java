package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class QueueManagerCommand implements CommandExecutor{

	private final Main main;

	public QueueManagerCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
				if (Bukkit.getServerName().toUpperCase().startsWith(ServerType.MINIGAME_HUB)) {
					if (args.length == 0) {
						player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Listing all queues..."));
						player.sendMessage(" ");
						player.sendMessage(CC.translate("&6■ &7Solo: " + main.getQueueManager().getSoloBW()));
						player.sendMessage(CC.translate("&6■ &7Standby: " + main.getQueueManager().getSoloStandby()));
						player.sendMessage(CC.translate("&6■ &7Priority: " + main.getQueueManager().getPriority()));
					} else if (args.length == 3) {
						String action = args[0];
						String type = args[1];
						String server = args[2];
						if (type.equalsIgnoreCase("bwsolo")) {
							if (action.equalsIgnoreCase("add")) {
								if (!main.getQueueManager().getSoloBW().contains(server)) {
									main.getQueueManager().getSoloBW().add(server);
									player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Server " + server + " added!"));
								}
							} else if (action.equalsIgnoreCase("remove")) {
								if (main.getQueueManager().getSoloBW().contains(server)) {
									main.getQueueManager().getSoloBW().remove(server);
									player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Server " + server + " removed!"));
								}
							} else {
								player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Valid action types: remove, add"));
							}
						} else if (type.equalsIgnoreCase("bwsolostandby")) {
							if (action.equalsIgnoreCase("add")) {
								if (!main.getQueueManager().getSoloStandby().contains(server)) {
									main.getQueueManager().getSoloStandby().add(server);
									player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Server " + server + " added!"));
								}
							} else if (action.equalsIgnoreCase("remove")) {
								if (main.getQueueManager().getSoloStandby().contains(server)) {
									main.getQueueManager().getSoloStandby().remove(server);
									player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Server " + server + " removed!"));
								}
							} else {
								player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Valid action types: remove, add"));
							}
						} else if (type.equalsIgnoreCase("bwsolopriority")) {
							if (action.equalsIgnoreCase("add")) {
								if (!main.getQueueManager().getPriority().contains(server)) {
									main.getQueueManager().getPriority().add(server);
									player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Server " + server + " added!"));
								}
							} else if (action.equalsIgnoreCase("remove")) {
								if (main.getQueueManager().getPriority().contains(server)) {
									main.getQueueManager().getPriority().remove(server);
									player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Server " + server + " removed!"));
								}
							} else {
								player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Valid action types: remove, add"));
							}
						} else {
							player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Valid types: bwsolo, bwsolostandby, bwsolopriority"));
						}
					} else {
						player.sendMessage(CC.translate("&e&lQUEUE &6■ &7Usage: /queuemanager (remove/add) (queuetype) (server)"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lQUEUE &6■ &7You are not allowed to use this command here!"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return false;
	}

}
