package me.thesquadmc.utils.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelpSubCommand extends PlayerSubCommand {

	private final Map<Integer, List<SubCommand>> pages = Maps.newHashMap();

	public HelpSubCommand(Command command) {
		super(command, "Find help here!", "[page]", null, "help", "?");
	}

	@Override
	public void execute(Player player, String label, String... args) {
		Integer page;

		if (args.length == 0) {
			page = 1;
		} else {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				page = 1;
			}
		}

		ComponentBuilder title = new ComponentBuilder("");

		if (page <= 1) {
			title.append("«").color(ChatColor.DARK_GRAY);

		} else {
			title.append("«").color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getCommand().getName() + " help " + (page - 1)));
		}

		if (page > pages.size()) {
			page = pages.size();
		}

		title.append(CC.GRAY + " Subcommands for " + CC.YELLOW + "/" + getCommand().getName().toLowerCase() + " ");

		if (page >= pages.size()) {
			title.append("»").color(ChatColor.DARK_GRAY);
		} else {
			title.append("»").color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getCommand().getName() + " help " + (page + 1)));
		}

		player.spigot().sendMessage(title.create());
		player.sendMessage(" ");
		sendHelpPage(player, page);
	}

	@Override
	public List<String> tabComplete(Player player, String... args) {
		return Collections.singletonList("page");
	}

	protected void calculatePages() {
		pages.clear();

		for (List<SubCommand> partition : Iterables.partition(getCommand().getSubCommands(), 5)) {
			pages.put(pages.size() + 1, partition);
		}
	}

	private void sendHelpPage(Player player, int page) {
		List<SubCommand> cmds = pages.get(page);
		if (cmds == null) {
			player.sendMessage(CC.RED + "Page #" + (page + 1) + " not found! There are: " + pages.size() + " pages!");
			return;
		}

		cmds.forEach(subCommand -> {
			String rawCommand = "/" + getCommand().getName() + " " + subCommand.getAlaiases().get(0);
			player.spigot().sendMessage(StringUtils.getSuggestiveMessage(CC.DARK_GRAY + "■ " + CC.YELLOW + rawCommand + " " + CC.GRAY + subCommand.getDescription(), CC.GRAY + rawCommand, rawCommand));
		});
	}

}
