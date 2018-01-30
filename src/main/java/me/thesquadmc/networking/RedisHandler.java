package me.thesquadmc.networking;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.commands.FindCommand;
import me.thesquadmc.commands.StafflistCommand;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.MessageSettings;
import me.thesquadmc.utils.RedisArg;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RedisHandler {

	private final Main main;

	public RedisHandler(Main main) {
		this.main = main;
	}

	public void processRedisMessage(JedisTask task, String channel, String message) {
		Map<String, Object> data = task.getData();
		if (channel.equalsIgnoreCase(RedisChannels.STAFFCHAT.toString())) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				User user = main.getLuckPermsApi().getUser(player.getUniqueId());
				TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
				if (main.hasPerm(user, "tools.staff.staffchat")) {
					if (tempData.isStaffchatEnabled() && tempData.getStaffchatSetting() == MessageSettings.GLOBAL) {
						player.sendMessage(StringUtils.msg(String.valueOf(data.get(RedisArg.MESSAGE.getArg()))));
					}
				}
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.FIND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getName().equalsIgnoreCase(name)) {
							TempData tempData = main.getTempDataManager().getTempData(p.getUniqueId());
							try (Jedis jedis = main.getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.SERVER.getArg(), String.valueOf(data.get(RedisArg.SERVER.getArg())))
										.withArg(RedisArg.ORIGIN_SERVER.getArg(), Bukkit.getServerName())
										.withArg(RedisArg.PLAYER.getArg(), name)
										.withArg(RedisArg.ORIGIN_PLAYER.getArg(), String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg())))
										.withArg(RedisArg.LOGIN.getArg(), tempData.getLoginTime())
										.send(RedisChannels.FOUND.getChannelName(), jedis);
							}
							return;
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FOUND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
					if (server.equalsIgnoreCase(Bukkit.getServerName())) {
						String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
						String origin = String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg()));
						String originServer = String.valueOf(data.get(RedisArg.ORIGIN_SERVER.getArg()));
						String time = String.valueOf(data.get(RedisArg.LOGIN.getArg()));
						Player p = Bukkit.getPlayer(origin);
						if (p != null) {
							FindCommand.getStillLooking().remove(p.getName());
							p.sendMessage(" ");
							p.sendMessage(StringUtils.msg("&6&l" + name));
							p.sendMessage(StringUtils.msg("&8■ &7Server: &f" + originServer));
							p.sendMessage(StringUtils.msg("&8■ &7Online Since: &f" + time));
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.REQUEST_LIST.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					ArrayList<String> trainee = new ArrayList<>();
					ArrayList<String> helper = new ArrayList<>();
					ArrayList<String> mod = new ArrayList<>();
					ArrayList<String> srmod = new ArrayList<>();
					ArrayList<String> admin = new ArrayList<>();
					ArrayList<String> manager = new ArrayList<>();
					ArrayList<String> developer = new ArrayList<>();
					ArrayList<String> owner = new ArrayList<>();
					StringBuilder tSB = new StringBuilder();
					StringBuilder hSB = new StringBuilder();
					StringBuilder mSB = new StringBuilder();
					StringBuilder srSB = new StringBuilder();
					StringBuilder aSB = new StringBuilder();
					StringBuilder manSB = new StringBuilder();
					StringBuilder dSB = new StringBuilder();
					StringBuilder oSB = new StringBuilder();
					for (Player p : Bukkit.getOnlinePlayers()) {
						TempData tempData = main.getTempDataManager().getTempData(p.getUniqueId());
						User user = main.getLuckPermsApi().getUser(p.getUniqueId());
						if (!tempData.isVanished()) {
							if (main.hasPerm(user, "stafflist.trainee")) {
								trainee.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.helper")) {
								helper.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.mod")) {
								mod.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.srmod")) {
								srmod.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.admin")) {
								admin.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.manager")) {
								manager.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.developer")) {
								developer.add(p.getName());
							} else if (main.hasPerm(user, "stafflist.owner")) {
								owner.add(p.getName());
							}
						}
					}
					if (!trainee.isEmpty()) {
						for (String s : trainee) {
							tSB.append(" " + s);
						}
					}
					if (!helper.isEmpty()) {
						for (String s : helper) {
							hSB.append(" " + s);
						}
					}
					if (!mod.isEmpty()) {
						for (String s : mod) {
							mSB.append(" " + s);
						}
					}
					if (!srmod.isEmpty()) {
						for (String s : srmod) {
							srSB.append(" " + s);
						}
					}
					if (!admin.isEmpty()) {
						for (String s : admin) {
							aSB.append(" " + s);
						}
					}
					if (!manager.isEmpty()) {
						for (String s : manager) {
							manSB.append(" " + s);
						}
					}
					if (!developer.isEmpty()) {
						for (String s : developer) {
							dSB.append(" " + s);
						}
					}
					if (!owner.isEmpty()) {
						for (String s : owner) {
							oSB.append(" " + s);
						}
					}
					try (Jedis jedis = main.getPool().getResource()) {
						JedisTask.withName(UUID.randomUUID().toString())
								.withArg(RedisArg.SERVER.getArg(), String.valueOf(data.get(RedisArg.SERVER.getArg())))
								.withArg(RedisArg.PLAYER.getArg(), String.valueOf(data.get(RedisArg.PLAYER.getArg())))
								.withArg(RedisArg.TRAINEE.getArg(), tSB.toString())
								.withArg(RedisArg.HELPER.getArg(), hSB.toString())
								.withArg(RedisArg.MOD.getArg(), mSB.toString())
								.withArg(RedisArg.SRMOD.getArg(), srSB.toString())
								.withArg(RedisArg.ADMIN.getArg(), aSB.toString())
								.withArg(RedisArg.MANAGER.getArg(), manSB.toString())
								.withArg(RedisArg.DEVELOPER.getArg(), dSB.toString())
								.withArg(RedisArg.OWNER.getArg(), oSB.toString())
								.send(RedisChannels.RETURN_REQUEST_LIST.getChannelName(), jedis);
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.RETURN_REQUEST_LIST.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
			if (server.equalsIgnoreCase(Bukkit.getServerName())) {
				Map<UUID, Map<RedisArg, String>> map = StafflistCommand.getStafflist();
				for (Map.Entry<UUID, Map<RedisArg, String>> m : map.entrySet()) {
					Player p = Bukkit.getPlayer(m.getKey());
					if (p.getName().equalsIgnoreCase(name)) {
						String trainee = String.valueOf(data.get(RedisArg.TRAINEE.getArg()));
						String helper = String.valueOf(data.get(RedisArg.HELPER.getArg()));
						String mod = String.valueOf(data.get(RedisArg.MOD.getArg()));
						String srmod = String.valueOf(data.get(RedisArg.SRMOD.getArg()));
						String admin = String.valueOf(data.get(RedisArg.ADMIN.getArg()));
						String manager = String.valueOf(data.get(RedisArg.MANAGER.getArg()));
						String developer = String.valueOf(data.get(RedisArg.DEVELOPER.getArg()));
						String owner = String.valueOf(data.get(RedisArg.OWNER.getArg()));
						if (m.getValue().get(RedisArg.TRAINEE) != null) {
							m.getValue().put(RedisArg.TRAINEE, m.getValue().get(RedisArg.TRAINEE) + trainee);
						} else {
							m.getValue().put(RedisArg.TRAINEE, trainee);
						}
						if (m.getValue().get(RedisArg.HELPER) != null) {
							m.getValue().put(RedisArg.HELPER, m.getValue().get(RedisArg.HELPER) + helper);
						} else {
							m.getValue().put(RedisArg.HELPER, helper);
						}
						if (m.getValue().get(RedisArg.MOD) != null) {
							m.getValue().put(RedisArg.MOD, m.getValue().get(RedisArg.MOD) + mod);
						} else {
							m.getValue().put(RedisArg.MOD, mod);
						}
						if (m.getValue().get(RedisArg.SRMOD) != null) {
							m.getValue().put(RedisArg.SRMOD, m.getValue().get(RedisArg.SRMOD) + srmod);
						} else {
							m.getValue().put(RedisArg.SRMOD, srmod);
						}
						if (m.getValue().get(RedisArg.ADMIN) != null) {
							m.getValue().put(RedisArg.ADMIN, m.getValue().get(RedisArg.ADMIN) + admin);
						} else {
							m.getValue().put(RedisArg.ADMIN, admin);
						}
						if (m.getValue().get(RedisArg.MANAGER) != null) {
							m.getValue().put(RedisArg.MANAGER, m.getValue().get(RedisArg.MANAGER) + manager);
						} else {
							m.getValue().put(RedisArg.MANAGER, manager);
						}
						if (m.getValue().get(RedisArg.DEVELOPER) != null) {
							m.getValue().put(RedisArg.DEVELOPER, m.getValue().get(RedisArg.DEVELOPER) + developer);
						} else {
							m.getValue().put(RedisArg.DEVELOPER, developer);
						}
						if (m.getValue().get(RedisArg.OWNER) != null) {
							m.getValue().put(RedisArg.OWNER, m.getValue().get(RedisArg.OWNER) + owner);
						} else {
							m.getValue().put(RedisArg.OWNER, owner);
						}
					}
				}
			}
		}
	}

}
