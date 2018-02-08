package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class PingCommand implements CommandExecutor {

	private final Main main;

	public PingCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			int ping = ((CraftPlayer)player).getHandle().ping;
			player.sendMessage(StringUtils.msg("&e&lPING &6■ &7Your ping is currently &e" + ping + "&7ms"));
		}
		return true;
	}

}
