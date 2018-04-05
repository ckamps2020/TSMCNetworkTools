package me.thesquadmc.networking;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.Sounds;
import me.thesquadmc.commands.FindCommand;
import me.thesquadmc.commands.FriendCommand;
import me.thesquadmc.commands.StafflistCommand;
import me.thesquadmc.commands.StaffmodeCommand;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.objects.Party;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.*;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.GameMsgs;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.server.ConnectionUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

import java.util.*;

public final class RedisHandler {

	private final Main main;

	public RedisHandler(Main main) {
		this.main = main;
	}

	public void processRedisMessage(JedisTask task, String channel, String message) {
		Map<String, Object> data = task.getData();
		if (channel.equalsIgnoreCase(RedisChannels.STAFFCHAT.toString())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
							TSMCUser user = TSMCUser.fromPlayer(player);
							if (user.getSetting(PlayerSetting.STAFFCHAT_ENABLED) && user.getSetting(PlayerSetting.STAFFCHAT_SCOPE) == MessageSettings.GLOBAL) {
								String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
								player.spigot().sendMessage(StringUtils.getHoverMessage(String.valueOf(data.get(RedisArg.MESSAGE.getArg())), "&7Currently on &e" + server));
							}
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.MANAGERCHAT.toString())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
							TSMCUser user = TSMCUser.fromPlayer(player);
							if (user.getSetting(PlayerSetting.MANAGERCHAT_ENABLED) && user.getSetting(PlayerSetting.MANAGERCHAT_SCOPE) == MessageSettings.GLOBAL) {
								player.sendMessage(CC.translate(String.valueOf(data.get(RedisArg.MESSAGE.getArg()))));
							}
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.ADMINCHAT.toString())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
							TSMCUser user = TSMCUser.fromPlayer(player);
							if (user.getSetting(PlayerSetting.ADMINCHAT_ENABLED) && user.getSetting(PlayerSetting.ADMINCHAT_SCOPE) == MessageSettings.GLOBAL) {
								player.sendMessage(CC.translate(String.valueOf(data.get(RedisArg.MESSAGE.getArg()))));
							}
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FIND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getName().equalsIgnoreCase(name)) {
							TSMCUser user = TSMCUser.fromPlayer(p);
							Multithreading.runAsync(new Runnable() {
								@Override
								public void run() {
									try (Jedis jedis = main.getPool().getResource()) {
										JedisTask.withName(UUID.randomUUID().toString())
												.withArg(RedisArg.SERVER.getArg(), String.valueOf(data.get(RedisArg.SERVER.getArg())))
												.withArg(RedisArg.ORIGIN_SERVER.getArg(), Bukkit.getServerName())
												.withArg(RedisArg.PLAYER.getArg(), name)
												.withArg(RedisArg.ORIGIN_PLAYER.getArg(), String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg())))
												.withArg(RedisArg.LOGIN.getArg(), user.getLoginTime())
												.send(RedisChannels.FOUND.getChannelName(), jedis);
									}
								}
							});
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
							p.sendMessage(CC.translate("&6&l" + name));
							p.sendMessage(CC.translate("&8■ &7Server: &f" + originServer));
							p.sendMessage(CC.translate("&8■ &7Online Since: &f" + time));
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
						if (PlayerUtils.doesRankMatch(p, Rank.TRAINEE)) {
							trainee.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.HELPER)) {
							helper.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.MOD)) {
							mod.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.SRMOD)) {
							srmod.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.ADMIN)) {
							admin.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.MANAGER)) {
							manager.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.DEVELOPER)) {
							developer.add(p.getName());
						} else if (PlayerUtils.doesRankMatch(p, Rank.OWNER)) {
							owner.add(p.getName());
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
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
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
		} else if (channel.equalsIgnoreCase(RedisChannels.ANNOUNCEMENT.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
				String msg = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.broadcastMessage(CC.translate("&8[&4&lALERT&8] &c" + msg));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.broadcastMessage(CC.translate("&7"));
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sounds.ENDERDRAGON_GROWL.bukkitSound(), 1.0f, 1.0f);
				}
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.STOP.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
				String msg = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.broadcastMessage(CC.translate("&e&lSTOP &6■ &7Server restarting in &e15 &7seconds for reason: &e" + msg));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.broadcastMessage(CC.translate("&7"));
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (PlayerUtils.isEqualOrHigherThen(p, Rank.MOD)) {
								TSMCUser user = TSMCUser.fromPlayer(p);
								if (StaffmodeCommand.getStaffmode().containsKey(p.getUniqueId())) {
									p.getInventory().clear();
									for (ItemStack itemStack : StaffmodeCommand.getStaffmode().get(p.getUniqueId())) {
										if (itemStack != null) {
											p.getInventory().addItem(itemStack);
										}
									}
									StaffmodeCommand.getStaffmode().remove(p.getUniqueId());
									p.setGameMode(GameMode.SURVIVAL);
									StaffmodeCommand.getStaffmode().remove(p.getUniqueId());
									p.performCommand("spawn");
									p.sendMessage(CC.translate("&e&lSTOP &6■ &7Due to server restart you have been taken out of staffmode"));
								}
								if (user.isVanished() || user.isYtVanished()) {
									PlayerUtils.showPlayerSpectator(p);
									user.setVanished(false);
									user.setYtVanished(false);
									p.sendMessage(CC.translate("&e&lSTOP &6■ &7Due to server restart you have been taken out of vanish"));
								}
							}
							p.playSound(p.getLocation(), Sounds.ANVIL_USE.bukkitSound(), 1.0f, 1.0f);
						}
					}
				});
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
					@Override
					public void run() {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.kickPlayer(CC.translate("&e&lSTOP &6■ &7Server restarting for reason: &e" + msg));
						}
					}
				}, 10 * 20);
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
					@Override
					public void run() {
						Bukkit.shutdown();
						Bukkit.broadcastMessage(CC.translate("&e&lSTOP &6■ &7Server restarting for reason: &e" + msg));
					}
				}, 15 * 20L);
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.WHITELIST_ADD.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
				String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						Bukkit.dispatchCommand(console, "minecraft:whitelist add " + name);
					}
				});
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.WHITELIST_REMOVE.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
				String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						Bukkit.dispatchCommand(console, "minecraft:whitelist remove " + name);
					}
				});
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.WHITELIST.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
				String onoff = String.valueOf(data.get(RedisArg.ONOFF.getArg()));
				String msg = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
				if (onoff.equalsIgnoreCase("ON")) {
					Bukkit.broadcastMessage(CC.translate("&e&lWHITELIST &6■ &7Whitelist has been enabled for reason: &e" + msg));
					ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							Bukkit.dispatchCommand(console, "minecraft:whitelist on");
						}
					});
					for (Player p : Bukkit.getOnlinePlayers()) {
						OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getName());
						main.setWhitelistMessage(CC.translate(msg));
						if (!PlayerUtils.isEqualOrHigherThen(p, Rank.MANAGER) && !Bukkit.getWhitelistedPlayers().contains(offlinePlayer)) {
							p.kickPlayer(CC.translate("&7Whitelist enabled \n&e" + msg));
						}
					}
				} else {
					Bukkit.broadcastMessage(CC.translate("&e&lWHITELIST &6■ &7Whitelist has been disabled for reason: &e" + msg));
					ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							Bukkit.dispatchCommand(console, "minecraft:whitelist off");
						}
					});
				}
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.MONITOR_REQUEST.getChannelName())) {
			String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
			if (server.equalsIgnoreCase(Bukkit.getServerName())) {
				Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
					@Override
					public void run() {
						Multithreading.runAsync(new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = Main.getMain().getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.SERVER.getArg(), server)
											.withArg(RedisArg.UPTIME.getArg(), TimeUtils.millisToRoundedTime(System.currentTimeMillis() - main.getStartup()))
											.withArg(RedisArg.COUNT.getArg(), String.valueOf(Bukkit.getOnlinePlayers().size()))
											.withArg(RedisArg.TPS.getArg(), ServerUtils.getTPS(0))
											.withArg(RedisArg.MESSAGE.getArg(), "&7TPS = &e" + ServerUtils.getTPS(0) + "&7, &7Memory = &e" + ServerUtils.getUsedMemory() + "&8/&e" + ServerUtils.getTotalMemory() + "&7")
											.send(RedisChannels.MONITOR_RETURN.getChannelName(), jedis);
								}
							}
						});
					}
				});
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.PROXY_RETURN.getChannelName())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
					String player = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					if (server.equalsIgnoreCase(Bukkit.getServerName())) {
						Player p = Bukkit.getPlayer(player);
						if (p != null) {

							String proxies = String.valueOf(data.get(RedisArg.PROXIES.getArg()));
							String count = String.valueOf(data.get(RedisArg.COUNT.getArg()));
							String regex = "[ ]+";
							String[] tokens = proxies.split(regex);
							p.sendMessage(CC.translate("&7"));
							for (String s : tokens) {
								p.sendMessage(CC.translate(s));
							}
							p.sendMessage(CC.translate("&7"));
							p.sendMessage(CC.translate("&e" + count + "&8/&e4000 &7Online globally"));
							p.sendMessage(CC.translate("&7"));
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.MONITOR_INFO.getChannelName())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
					String count = String.valueOf(data.get(RedisArg.COUNT.getArg()));
					String msg = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
					String uptime = String.valueOf(data.get(RedisArg.UPTIME.getArg()));
					String tps = String.valueOf(data.get(RedisArg.TPS.getArg()));
					if (!uptime.equalsIgnoreCase("0")) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN) && TSMCUser.fromPlayer(player).hasMonitor()) {
								if (!tps.equalsIgnoreCase("null") && Double.valueOf(tps) > 15.0) {
									player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
									player.sendMessage(CC.translate("&6&l[MONITOR REPORT] &f" + server + " &7" + count + "&8/&7200"));
									player.sendMessage(CC.translate("&7"));
									player.sendMessage(CC.translate(msg));
									player.sendMessage(CC.translate("&7Uptime = &e" + uptime));
									player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
								} else {
									player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
									player.sendMessage(CC.translate("&6&l[MONITOR REPORT] &f" + server + " &cBelow 15 TPS!"));
									player.sendMessage(CC.translate("&7"));
									player.sendMessage(CC.translate(msg));
									player.sendMessage(CC.translate("&7Uptime = &e" + uptime));
									player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
								}
							}
						}
					} else {
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN) && TSMCUser.fromPlayer(player).hasMonitor()) {
								player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
								player.sendMessage(CC.translate("&6&l[MONITOR REPORT] &f" + server + " &7" + count + "&8/&7200"));
								player.sendMessage(CC.translate("&7"));
								player.sendMessage(CC.translate(msg));
								player.sendMessage(CC.translate("&8&m-------------------------------------------------"));
							}
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FRIEND_CHECK_REQUEST.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getName().equalsIgnoreCase(name)) {
							Multithreading.runAsync(new Runnable() {
								@Override
								public void run() {
									try (Jedis jedis = main.getPool().getResource()) {
										JedisTask.withName(UUID.randomUUID().toString())
												.withArg(RedisArg.SERVER.getArg(), String.valueOf(data.get(RedisArg.SERVER.getArg())))
												.withArg(RedisArg.PLAYER.getArg(), name)
												.withArg(RedisArg.ORIGIN_PLAYER.getArg(), String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg())))
												.send(RedisChannels.FRIEND_RETURN_REQUEST.getChannelName(), jedis);
									}
								}
							});
							return;
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FRIEND_RETURN_REQUEST.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
							if (server.equalsIgnoreCase(Bukkit.getServerName())) {
								String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
								String origin = String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg()));
								Player p = Bukkit.getPlayer(origin);
								if (p != null) {
									FriendCommand.online.get(p.getUniqueId()).add(name);
								}
							}
						}
					});
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FRIEND_REMOVE_OUTBOUND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getName().equalsIgnoreCase(name)) {
							OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg())));
							TSMCUser.fromPlayer(p).removeFriend(offlinePlayer);
							p.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + offlinePlayer.getName() + " &7has removed you as a friend"));
							Multithreading.runAsync(new Runnable() {
								@Override
								public void run() {
									try (Jedis jedis = main.getPool().getResource()) {
										JedisTask.withName(UUID.randomUUID().toString())
												.withArg(RedisArg.SERVER.getArg(), String.valueOf(data.get(RedisArg.SERVER.getArg())))
												.withArg(RedisArg.PLAYER.getArg(), name)
												.withArg(RedisArg.ORIGIN_PLAYER.getArg(), String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg())))
												.send(RedisChannels.FRIEND_REMOVE_INBOUND.getChannelName(), jedis);
									}
								}
							});
							try {
								main.getMySQL().saveFriendAccount(p.getUniqueId().toString());
							} catch (Exception e) {
								System.out.println("[NetworkTools] Unable to execute mysql operation");
							}
							return;
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FRIEND_REMOVE_INBOUND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
					if (server.equalsIgnoreCase(Bukkit.getServerName())) {
						String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
						String origin = String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg()));
						Player p = Bukkit.getPlayer(origin);
						if (p != null) {
							FriendCommand.getStillLooking().remove(p.getName());
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FRIEND_CHAT.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
							String friends = String.valueOf(data.get(RedisArg.FRIENDS.getArg()));
							String player = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
							String message = String.valueOf(data.get(RedisArg.MESSAGE.getArg()));
							String ss = String.valueOf(data.get(RedisArg.SSMSG.getArg()));
							List<String> f = new ArrayList<>();
							String regex = "[ ]+";
							String[] tokens = friends.split(regex);
							for (String s : tokens) {
								f.add(s);
							}
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (PlayerUtils.isEqualOrHigherThen(p, Rank.TRAINEE) && TSMCUser.fromPlayer(p).getSetting(PlayerSetting.SOCIALSPY)) {
									p.spigot().sendMessage(StringUtils.getHoverMessage(ss, "&7Currently on &d" + server));
								}
							}
							for (String s : f) {
								if (UUID.fromString(s) != null) {
									Player t = Bukkit.getPlayer(UUID.fromString(s));
									if (t != null) {
										t.spigot().sendMessage(StringUtils.getHoverMessage(message, "&7Currently on &d" + server));
									}
								}
							}
						}
					});
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.LOGIN.getChannelName())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					String player = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (TSMCUser.fromPlayer(p).isFriend(offlinePlayer)) {
							p.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player + " &7has logged in!"));
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.LEAVE.getChannelName())) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					String player = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (TSMCUser.fromPlayer(p).isFriend(offlinePlayer)) {
							p.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player + " &7has logged out!"));
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.RETURN_SERVER.getChannelName())) {
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							String server = String.valueOf(data.get(RedisArg.ORIGIN_SERVER.getArg()));
							if (Bukkit.getServerName().equalsIgnoreCase(server)) {
								String player = String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg()));
								String newServer = String.valueOf(data.get(RedisArg.SERVER.getArg()));
								if (newServer.equalsIgnoreCase("NONE")) {
									if (Bukkit.getPlayer(player) != null) {
										Player p = Bukkit.getPlayer(player);
										ConnectionUtils.getFetching().remove(p.getUniqueId());
										p.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Unable to find an open server right now! Please try again"));
									}
								} else {
									if (Bukkit.getPlayer(player) != null) {
										Player p = Bukkit.getPlayer(player);
										ConnectionUtils.getFetching().remove(p.getUniqueId());
										ConnectionUtils.sendPlayer(p, newServer, true);
									}
								}
							}
						}
					});
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.STARTUP_REQUEST.getChannelName())) {
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							ServerUtils.updateServerState(main.getServerState());
						}
					});
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.PLAYER_COUNT.getChannelName())) {
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
							Double count = Double.valueOf(String.valueOf(data.get(RedisArg.COUNT.getArg())));
							main.getCountManager().getCount().put(server, count.intValue());
						}
					});
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.PARTY_JOIN_SERVER.getChannelName())) {
			Bukkit.getScheduler().runTaskAsynchronously(main, () -> Multithreading.runAsync(() -> {
				Party party = Main.getMain().getGson().fromJson(String.valueOf(data.get(RedisArg.PARTY.getArg())), Party.class);
				if (party == null) return;
				
				Main.getMain().getPartyManager().addParty(party);
			}));
		} else if (channel.equalsIgnoreCase(RedisChannels.PARTY_UPDATE.getChannelName())) {
			Bukkit.getScheduler().runTaskAsynchronously(main, () -> Multithreading.runAsync(() -> {
				Party party = Main.getMain().getGson().fromJson(String.valueOf(data.get(RedisArg.PARTY.getArg())), Party.class);
				if (party == null) return;

				// Looks ugly, but it works
				PartyManager manager = Main.getMain().getPartyManager();
				if (manager.removeParty(party)) {
					manager.addParty(party);
				}
			}));
		} else if (channel.equalsIgnoreCase(RedisChannels.PARTY_DISBAND.getChannelName())) {
			Bukkit.getScheduler().runTaskAsynchronously(main, () -> Multithreading.runAsync(() -> {
				Party party = Main.getMain().getGson().fromJson(String.valueOf(data.get(RedisArg.PARTY.getArg())), Party.class);
				if (party == null) return;
				
				Main.getMain().getPartyManager().removeParty(party);
				for (OfflinePlayer member : party.getMembers()) {
					if (!member.isOnline()) return;
					member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7Your &eparty &7has been &edisbanded&7!"));
				}
			}));
		}
	}

}
