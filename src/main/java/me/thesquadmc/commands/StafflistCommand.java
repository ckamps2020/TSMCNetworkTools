package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.RedisArg;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StafflistCommand implements CommandExecutor {

	private final Main main;
	private static Map<UUID, Map<RedisArg, String>> stafflist = new HashMap<>();

	public StafflistCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					stafflist.put(player.getUniqueId(), new HashMap<>());
					try (Jedis jedis = main.getPool().getResource()) {
						JedisTask.withName(UUID.randomUUID().toString())
								.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
								.withArg(RedisArg.PLAYER.getArg(), player.getName())
								.send(RedisChannels.REQUEST_LIST.getChannelName(), jedis);
						Bukkit.getScheduler().runTaskLater(main, () -> {
							String trainee = "";
							String helper = "";
							String mod = "";
							String srmod = "";
							String admin = "";
							String manager = "";
							String developer = "";
							String owner = "";
							for (Map.Entry<RedisArg, String> m : stafflist.get(player.getUniqueId()).entrySet()) {
								if (m.getKey() == RedisArg.TRAINEE) {
									trainee = m.getValue();
								} else if (m.getKey() == RedisArg.HELPER) {
									helper = m.getValue();
								} else if (m.getKey() == RedisArg.MOD) {
									mod = m.getValue();
								} else if (m.getKey() == RedisArg.SRMOD) {
									srmod = m.getValue();
								} else if (m.getKey() == RedisArg.ADMIN) {
									admin = m.getValue();
								} else if (m.getKey() == RedisArg.MANAGER) {
									manager = m.getValue();
								} else if (m.getKey() == RedisArg.DEVELOPER) {
									developer = m.getValue();
								} else if (m.getKey() == RedisArg.OWNER) {
									owner = m.getValue();
								}
							}
							String tr = "[ ]+";
							String hr = "[ ]+";
							String mr = "[ ]+";
							String srr = "[ ]+";
							String ar = "[ ]+";
							String manr = "[ ]+";
							String dr = "[ ]+";
							String or = "[ ]+";
							String[] ttokens = trainee.split(tr);
							String[] htokens = helper.split(hr);
							String[] mtokens = mod.split(mr);
							String[] srtokens = srmod.split(srr);
							String[] atokens = admin.split(ar);
							String[] mantokens = manager.split(manr);
							String[] dtokens = developer.split(dr);
							String[] otokens = owner.split(or);
							if (!trainee.equalsIgnoreCase("")) {
								player.spigot().sendMessage(StringUtils.getHoverMessage("&a" + (ttokens.length - 1) + " &8[&f&lTrainee&8]&7" + trainee, "&7Want to become &eTrainee&7? Apply at:\n" +
										"&6&nwww.thesquadmc.net/forums/staff-applications"));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&f&lTrainee&8] &7None"));
							}
							if (!helper.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (htokens.length - 1) + " &8[&3&lHelper&8]&7" + helper));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&3&lHelper&8] &7None"));
							}
							if (!mod.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (mtokens.length - 1) + " &8[&5&lMod&8]&7" + mod));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&5&lMod&8] &7None"));
							}
							if (!srmod.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (srtokens.length - 1) + " &8[&d&lSr-Mod&8]&7" + srmod));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&d&lSr-Mod&8] &7None"));
							}
							if (!admin.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (atokens.length - 1) + " &8[&c&lAdmin&8]&7" + admin));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&c&lAdmin&8] &7None"));
							}
							if (!manager.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (mantokens.length - 1) + " &8[&c&lManager&8]&7" + manager));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&c&lManager&8] &7None"));
							}
							if (!developer.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (dtokens.length - 1) + " &8[&c&lDeveloper&8] &7" + developer));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&c&lDeveloper&8] &7None"));
							}
							if (!owner.equalsIgnoreCase("")) {
								player.sendMessage(StringUtils.msg("&a" + (otokens.length - 1) + " &8[&4&lOwner&8]&7" + owner));
							} else {
								player.sendMessage(StringUtils.msg("&c0 &8[&4&lOwner&8] &7None"));
							}
							//fetch online count
							stafflist.remove(player.getUniqueId());
						}, 20L);
					}
				}
			});
		}
		return true;
	}

	public static Map<UUID, Map<RedisArg, String>> getStafflist() {
		return stafflist;
	}

}
