package me.thesquadmc.utils.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Command extends org.bukkit.command.Command {

	private static final String NO_PERMISSION_MESSAGE = CC.B + CC.RED + "You do not have permission to run this command!";

	/**
	 * Instance of our main class
	 **/
	private final Main plugin;

	/**
	 * Map of all sub commands, aliases are mapped as well
	 **/
	private final Set<SubCommand> subCommands = Sets.newHashSet();

	/**
	 * Default command that is ran when no args are provided
	 **/
	private SubCommand defaultCommand;

	/**
	 * Usage message in case there is a syntax error
	 **/
	private String usage = "";

	/**
	 * Whether console can run this command or not
	 **/
	private boolean allowConsole;

	private HelpSubCommand helpSubCommand;

	public Command(Main plugin, String description, String permission, boolean allowConsole, String usage, String... aliases) {
		super(aliases[0]);
		this.plugin = plugin;
		this.allowConsole = allowConsole;

		setDescription(description);
		setUsage(usage);
		setPermission(permission);
		setAliases(Arrays.asList(aliases));

		helpSubCommand = new HelpSubCommand(this);
		registerSubCommand(helpSubCommand);

		defaultCommand = helpSubCommand;
		getPlugin().getCommandManager().getCommandMap().register("", this);
	}

	public Command(Main plugin, String description, String permission, String... aliases) {
		this(plugin, description, permission, false, "", aliases);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		boolean isConsole = sender instanceof ConsoleCommandSender;

		if (args.length == 0) {
			if (isConsole && !allowConsole) {
				sender.sendMessage(CC.RED + "Only players can run this command!");
				return false;
			}

			if (defaultCommand != null) {
				System.out.println("there is a def cmd");
				defaultCommand.execute(sender, commandLabel, args);
				return true;
			}

			sender.sendMessage(CC.GRAY + "■ " + CC.YELLOW + "Running " + plugin.getDescription().getFullName());
			sender.sendMessage(CC.GRAY + "■ " + CC.YELLOW + "/" + commandLabel + " help to view all subcommands!");
			return false;
		}

		if (!subCommands.isEmpty()) {
			SubCommand subCommand;

			subCommand = getSubCommand(args[0]);

			if (subCommand == null) {
				subCommand = defaultCommand;
			}

			if (subCommand instanceof PlayerSubCommand) {
				if (isConsole) {
					sender.sendMessage(CC.RED + "Only players can run this command!");
					return false;
				}
			}

			if (subCommand.getPermission() != null) {
				if (!sender.hasPermission(subCommand.getPermission())) {
					sender.sendMessage(NO_PERMISSION_MESSAGE);
					return false;
				}
			}

			subCommand.execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
			return true;
		}

		return false;
	}

	/**
	 * Tab completes with closest subcommand, if a subcommand
	 * is already present will use that command's tab complete method.
	 */
	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
		if (!(sender instanceof Player)) {
			return null;
		}

		if (args.length > 0) {
			if (args.length == 1) {
				return findClosest(subCommands, args[0]);

			} else {
				SubCommand sub = getSubCommand(args[0]);

				if (sub == null) {
					return null;
				}

				Player player = (Player) sender;
				List<String> tab = sub.tabComplete(player, Arrays.copyOfRange(args, 1, args.length));

				if (tab != null) {
					return tab;
				}
			}
		}
		return super.tabComplete(sender, args[0], args);
	}

	private SubCommand getSubCommand(String label) {
		for (SubCommand subCommand : subCommands) {
			for (String name : subCommand.getAlaiases()) {
				if (name.equalsIgnoreCase(label)) {
					return subCommand;
				}
			}
		}

		return null;
	}

	public Main getPlugin() {
		return plugin;
	}

	private List<String> findClosest(Set<SubCommand> cmds, String startsWith) {
		List<String> closest = Lists.newArrayList();

		if (startsWith == null || startsWith.isEmpty()) {
			cmds.forEach(subCommand -> closest.addAll(subCommand.getAlaiases()));
		} else {
			for (SubCommand cmd : cmds) {
				for (String string : cmd.getAlaiases()) {
					if (string.toLowerCase().startsWith(startsWith.toLowerCase())) {
						closest.add(string);
					}
				}
			}
		}

		return closest;
	}

	protected void registerSubCommand(SubCommand... subCommand) {
		Arrays.stream(subCommand).forEach(cmd -> {
			cmd.getAlaiases().forEach(name -> subCommands.add(cmd));
		});

		helpSubCommand.calculatePages();
	}

	public Set<SubCommand> getSubCommands() {
		return subCommands.stream().filter(subCommand -> !(subCommand instanceof HelpSubCommand)).collect(Collectors.toSet());
	}

	public void setDefaultCommand(SubCommand defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

	public SubCommand getDefaultCommand() {
		return defaultCommand;
	}

	@Override
	public String getUsage() {
		return usage;
	}

	public boolean isAllowConsole() {
		return allowConsole;
	}

}
