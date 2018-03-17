package me.thesquadmc.utils.command;

import com.google.common.collect.Maps;
import me.thesquadmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class CommandManager implements Listener {

	/** Main instance of our class **/
	private final Main plugin;

	/** Map of all our commands, aliases are also mapped **/
	private final Map<String, Command> commands = Maps.newHashMap();

	private CommandMap commandMap;

	public CommandManager(Main plugin) {
		this.plugin = plugin;

		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);

				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Register a command
	 */
	public void registerCommmand(Command... command) {
		Arrays.stream(command).forEach(cmd -> cmd.getAliases().forEach(name -> commands.put(name, cmd)));
	}

	public CommandMap getCommandMap() {
		return commandMap;
	}

}
