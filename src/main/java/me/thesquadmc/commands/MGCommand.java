package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.math.MathUtils;
import me.thesquadmc.utils.msgs.ServerType;
import me.thesquadmc.utils.server.ConnectionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MGCommand implements CommandExecutor {

	private final Main main;

	public MGCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			int i = MathUtils.random(1, 5);
			String s = ServerType.MINIGAME_HUB + "-" + i;
			ConnectionUtils.sendPlayer(player, s);
		}
		return true;
	}

}
