package me.thesquadmc.utils.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerSubCommand extends SubCommand {

	public PlayerSubCommand(Command command, String description, String usage, String permission, String... alaiases) {
		super(command, description, usage, permission, alaiases);
	}

	public abstract void execute(Player player, String label, String... args);

	@Override
	public void execute(CommandSender sender, String label, String... args) {
		if (!(sender instanceof Player)) {
			throw new IllegalArgumentException("Sender must be an instance of Player");
		}

		execute((Player) sender, label, args);
	}

}
