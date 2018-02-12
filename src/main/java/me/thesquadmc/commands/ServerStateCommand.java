package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.ServerState;
import me.thesquadmc.utils.ServerUtils;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ServerStateCommand implements CommandExecutor {

	private final Main main;

	public ServerStateCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 1) {
				String state = args[0];
				if (state.equalsIgnoreCase(ServerState.LOADING)) {
					ServerUtils.updateServerState(ServerState.LOADING);
				} else if (state.equalsIgnoreCase(ServerState.ONLINE)) {
					ServerUtils.updateServerState(ServerState.ONLINE);
				} else if (state.equalsIgnoreCase(ServerState.ONLINE_MAX_PLAYERS)) {
					ServerUtils.updateServerState(ServerState.ONLINE_MAX_PLAYERS);
				} else if (state.equalsIgnoreCase(ServerState.MIDGAME)) {
					ServerUtils.updateServerState(ServerState.MIDGAME);
				} else if (state.equalsIgnoreCase(ServerState.RESTARTING)) {
					ServerUtils.updateServerState(ServerState.RESTARTING);
				} else {
					sender.sendMessage(StringUtils.msg("&c" + state + " is not a valid server state!"));
					return true;
				}
				sender.sendMessage(StringUtils.msg("&aServer state updated to: " + state));
			}
		} else {
			sender.sendMessage(StringUtils.msg("&cYou are not allowed to use this command in game!"));
		}
		return true;
	}

}
