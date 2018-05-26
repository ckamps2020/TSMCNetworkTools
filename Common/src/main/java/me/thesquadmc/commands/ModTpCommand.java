package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ModTpCommand implements CommandExecutor {

	private final Main main;

	public ModTpCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (!StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
				player.sendMessage(CC.RED + "You cannot do this!");
				return false;
			}

			if (args.length == 0) {
				player.sendMessage(CC.RED + "You have not provided a player!");
				return false;
			}

			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				player.sendMessage(CC.RED + "Cannot find " + args[0]);
				return false;
			}

			player.teleport(target.getLocation());
			return true;
		}

		return false;
	}

}
