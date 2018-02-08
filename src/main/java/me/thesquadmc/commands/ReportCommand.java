package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.Report;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public final class ReportCommand implements CommandExecutor {

	private final Main main;
	private ArrayList<UUID> cooldown = new ArrayList<>();

	public ReportCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length >= 2) {
				String name = args[0];
				Player t = Bukkit.getPlayer(name);
				if (t != null) {
					StringBuilder stringBuilder = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						stringBuilder.append(args[i] + " ");
					}
					String s = stringBuilder.toString().replaceAll(",", "");
					String regex = "[ ]+";
					String[] tokens = s.split(regex);
					for (String ss : tokens) {
						if (!main.getReportManager().isValidReportType(ss)) {
							player.sendMessage(StringUtils.msg("&cThis is not a valid reason, you can only report a player for the following reasons. (Commas for multiple reasons are supported)"));
							player.sendMessage(" ");
							player.sendMessage(StringUtils.msg("&eKillAura, Reach, Fly, Glide, AutoClicker, Speed, AntiKnockback, Jesus, Dolphin, Criticals, vClip, hClip, NoFall, Phase, Sneak & Fastbow"));
							player.sendMessage(" ");
							player.sendMessage(StringUtils.msg("&4&lAbuse of the report system will lead to a punishment"));
							player.sendMessage(StringUtils.msg("&cYou can report rule breakers here as well:\n" +
									"&fhttps://www.thesquadmc.net/forums/player-reports/"));
							return true;
						}
					}
					if (cooldown.contains(player.getUniqueId())) {
						player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7Please slowdown using the report command!"));
						return true;
					}
					cooldown.add(player.getUniqueId());
					main.getReportManager().newReport(new Report(t.getName(), StringUtils.getDate(), player.getName(), Bukkit.getServerName(), tokens));
					player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7You reported &e" + t.getName() + " &7for &e" + stringBuilder.toString() + "&7"));
					Bukkit.getScheduler().runTaskLater(main, new Runnable() {
						@Override
						public void run() {
							cooldown.remove(player.getUniqueId());
						}
					}, 20 * 20L);
				} else {
					player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7This player does not exist"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&eUsage: /report <player> <reason(s)>"));
				player.sendMessage(" ");
				player.sendMessage(StringUtils.msg("&eKillAura, Reach, Fly, Glide, AutoClicker, Speed, AntiKnockback, Jesus, Dolphin, Criticals, vClip, hClip, NoFall, Phase, Sneak & Fastbow"));
				player.sendMessage(" ");
				player.sendMessage(StringUtils.msg("&4&lAbuse of the report system will lead to a punishment"));
				player.sendMessage(StringUtils.msg("&cYou can report rule breakers here as well:\n" +
						"&fhttps://www.thesquadmc.net/forums/player-reports/"));
			}
		}
		return true;
	}

}
