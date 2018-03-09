package me.thesquadmc.utils.command;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {

	/** The parent command **/
	private final Command command;

	/** Description of the sub command **/
	private final String description;

	/** Permission needed to run this sub command**/
	private final String permission;

	/** List of aliases that can be used **/
	private final List<String> alaiases;

	/** Usage of the command **/
	private final String usage;

	public SubCommand(Command command, String description, String usage, String permission, String... alaiases) {
		this.command = command;
		this.description = description;
		this.usage = usage;
		this.permission = permission;
		this.alaiases = Arrays.asList(alaiases);
	}

	public abstract void execute(CommandSender sender, String label, String... args);

	public abstract List<String> tabComplete(Player player, String... args);

	public String getUsage(String label) {
		return CC.RED + "Usage: /" + label + " " + CC.YELLOW + usage;
	}

	public Main getPlugin() {
		return getCommand().getPlugin();
	}

	public Command getCommand() {
		return command;
	}

	public String getDescription() {
		return description;
	}

	public String getPermission() {
		return permission;
	}

	public List<String> getAlaiases() {
		return alaiases;
	}

}
