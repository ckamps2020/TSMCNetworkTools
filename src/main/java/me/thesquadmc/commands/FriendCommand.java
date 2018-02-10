package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.inventories.SettingsMenu;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.StringUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.Settings;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.*;

public final class FriendCommand implements CommandExecutor {

	private final Main main;
	public static Map<UUID, List<String>> online = new HashMap<>();
	private static List<String> stillLooking = new ArrayList<>();

	public FriendCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
				sendHelpMessage(player);
			} else if (args.length == 1) {
				String action = args[0];
				if (action.equalsIgnoreCase("requests")) {
					List<String> request = main.getRequests().get(player.getUniqueId());
					if (request != null && !request.isEmpty()) {
						player.sendMessage(StringUtils.msg("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
						for (String s : request) {
							if (Bukkit.getPlayer(UUID.fromString(s)) != null) {
								player.spigot().sendMessage(new ComponentBuilder(StringUtils.msg("&8[&d✔&8]"))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + Bukkit.getPlayer(UUID.fromString(s)).getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&dClick to accept the friend request"))))
										.append(StringUtils.msg(" &8[&c✖&8]"))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + Bukkit.getPlayer(UUID.fromString(s)).getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&cClick to deny the friend request"))))
										.append(StringUtils.msg(" &7" + Bukkit.getPlayer(UUID.fromString(s)).getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&7Pending friend request"))))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests"))
										.create());
							}
						}
						player.sendMessage(StringUtils.msg("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
					} else {
						player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have no pending friend requests!"));
					}
				} else if (action.equalsIgnoreCase("chat")) {
					Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
						@Override
						public void run() {
							if (main.getSettings().get(player.getUniqueId()).get(Settings.FRIENDCHAT)) {
								main.getSettings().get(player.getUniqueId()).put(Settings.FRIENDCHAT, false);
								player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have toggled friend chat mode &doff"));
							} else {
								main.getSettings().get(player.getUniqueId()).put(Settings.FRIENDCHAT, true);
								player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have toggled friend chat mode &don"));
							}
						}
					});
				} else if (action.equalsIgnoreCase("list")) {
					if (!main.getFriends().get(player.getUniqueId()).isEmpty()) {
						if (!online.containsKey(player.getUniqueId())) {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7Fetching friends list..."));
							Bukkit.getScheduler().runTaskLater(main, new Runnable() {
								@Override
								public void run() {
									List<String> strings = new ArrayList<>();
									for (String sss : main.getFriends().get(player.getUniqueId())) {
										strings.add(sss);
									}
									player.sendMessage(StringUtils.msg("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
									if (!strings.isEmpty()) {
										for (String s : strings) {
											if (s.equalsIgnoreCase(" ") || s.equalsIgnoreCase("")) {
												continue;
											}
											if (UUID.fromString(s) == null || Bukkit.getOfflinePlayer(UUID.fromString(s)) == null) {
												continue;
											}
											OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(s));
											player.sendMessage(StringUtils.msg("&7" + offlinePlayer.getName()));
										}
									}
									if (online.containsKey(player.getUniqueId())) {
										online.remove(player.getUniqueId());
									}
									player.sendMessage(StringUtils.msg("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
								}
							}, 1L);
						} else {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You are currently listing friends, please standby..."));
						}
					} else {
						player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have no friends! Get started by adding some!"));
					}
				} else if (action.equalsIgnoreCase("settings")) {
					SettingsMenu.buildSettingsMenu(player);
				} else if (action.equalsIgnoreCase("chatsocialspy")) {
					if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
						if (main.getSettings().get(player.getUniqueId()).get(Settings.SOCIALSPY)) {
							main.getSettings().get(player.getUniqueId()).put(Settings.SOCIALSPY, false);
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have toggled friend social spy &doff"));
						} else {
							main.getSettings().get(player.getUniqueId()).put(Settings.SOCIALSPY, true);
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have toggled friend social spy &don"));
						}
					} else {
						player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
					}
				} else {
					sendHelpMessage(player);
				}
			} else if (args.length == 2) {
				String action = args[0];
				String name = args[1];
				if (action.equalsIgnoreCase("add")) {
					if (main.getFriends().get(player.getUniqueId()).contains(Bukkit.getOfflinePlayer(name).getUniqueId().toString())) {
						player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7That person is already on your friends list!"));
						return true;
					}
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (target.getUniqueId() == player.getUniqueId()) {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You can't add yourself as a friend silly!"));
							return true;
						}
						if (!main.getSettings().get(target.getUniqueId()).get(Settings.REQUESTS)) {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7" + target.getName() + " currently has their friend requests disabled!"));
							return true;
						}
						List<String> targetRequests = main.getRequests().get(target.getUniqueId());
						if (targetRequests != null) {
							if (!targetRequests.contains(player.getUniqueId().toString())) {
								targetRequests.add(player.getUniqueId().toString());
								target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + player.getName() + " &7has sent you a friend request"));
								target.spigot().sendMessage(new ComponentBuilder(StringUtils.msg("&d&l[ACCEPT]"))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&dClick to accept the friend request"))))
										.append(StringUtils.msg(" &c&l[DECLINE]"))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&cClick to deny the friend request"))))
										.create());
								player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7A friend request has been sent to &d" + target.getName()));
								Bukkit.getScheduler().runTaskLaterAsynchronously(main, new Runnable() {
									@Override
									public void run() {
										if (main.getRequests().get(target.getUniqueId()) != null && main.getRequests().get(target.getUniqueId()).contains(player.getUniqueId().toString())) {
											main.getRequests().get(target.getUniqueId()).remove(player.getUniqueId().toString());
											target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7Your friend request from " + player.getName() + " has expired"));
											if (isOnline(player.getName())) {
												player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You friend request to " + target.getName() + " has expired"));
											}
										} else if (main.getFriends().get(player.getUniqueId()) != null && !main.getFriends().get(player.getUniqueId()).contains(target.getUniqueId().toString())) {
											player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You friend request to " + target.getName() + " has expired"));
										}
									}
								}, 300 * 20L);
							} else {
								player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have already sent " + target.getName() + " a request in the last 5 minutes!"));
							}
						} else {
							targetRequests = new ArrayList<>();
							targetRequests.add(player.getUniqueId().toString());
							main.getRequests().put(target.getUniqueId(), targetRequests);
							target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + player.getName() + " &7has sent you a friend request"));
							target.spigot().sendMessage(new ComponentBuilder(StringUtils.msg("&d&l[ACCEPT]"))
									.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName()))
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&dClick to accept the friend request"))))
									.append(StringUtils.msg(" &c&l[DECLINE]"))
									.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName()))
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(StringUtils.msg("&cClick to deny the friend request"))))
									.create());
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7A friend request has been sent to &d" + target.getName()));
							Bukkit.getScheduler().runTaskLaterAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									if (main.getRequests().get(target.getUniqueId()) != null && main.getRequests().get(target.getUniqueId()).contains(player.getUniqueId().toString())) {
										main.getRequests().get(target.getUniqueId()).remove(player.getUniqueId().toString());
										target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7Your friend request from " + player.getName() + " has expired"));
										if (isOnline(player.getName())) {
											player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You friend request to " + target.getName() + " has expired"));
										}
									} else if (main.getFriends().get(player.getUniqueId()) != null && !main.getFriends().get(player.getUniqueId()).contains(target.getUniqueId().toString())) {
										player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You friend request to " + target.getName() + " has expired"));
									}
								}
							}, 300 * 20L);
						}
					} else {
						player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + name + " &7is not online or does not exist!"));
					}
				} else if (action.equalsIgnoreCase("deny")) {
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (main.getRequests().get(player.getUniqueId()) != null && main.getRequests().get(player.getUniqueId()).contains(target.getUniqueId().toString())) {
							main.getRequests().get(player.getUniqueId()).remove(target.getUniqueId().toString());
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You have declined &d" + target.getName() + "’s &7friend request"));
							target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + player.getName() + " &7has declined your friend request"));
						} else {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You do not have a pending invite from " + target.getName()));
						}
					} else {
						player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You do not have a pending invite from " + name));
					}
				} else if (action.equalsIgnoreCase("remove")) {
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (main.getFriends().get(player.getUniqueId()).contains(target.getUniqueId().toString())) {
							main.getFriends().get(player.getUniqueId()).remove(target.getUniqueId().toString());
							main.getFriends().get(target.getUniqueId()).remove(player.getUniqueId().toString());
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + target.getName() + " &7is no longer a friend"));
							target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + player.getName() + " &7has removed you as a friend"));
							Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									try {
										main.getMySQL().saveFriendAccount(player.getUniqueId().toString());
										main.getMySQL().saveFriendAccount(target.getUniqueId().toString());
									} catch (Exception e) {
										System.out.println("[NetworkTools] Unable to execute mysql operation");
									}
								}
							});
						} else {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + name + " &7is not on your friends list!"));
						}
					} else {
						Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
							@Override
							public void run() {
								OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
								if (main.getFriends().get(player.getUniqueId()).contains(offlinePlayer.getUniqueId().toString())) {
									main.getFriends().get(player.getUniqueId()).remove(offlinePlayer.getUniqueId().toString());
									player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + name + " &7is no longer a friend"));
									stillLooking.add(player.getName());
									try (Jedis jedis = main.getPool().getResource()) {
										JedisTask.withName(UUID.randomUUID().toString())
												.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
												.withArg(RedisArg.PLAYER.getArg(), name)
												.withArg(RedisArg.ORIGIN_PLAYER.getArg(), player.getName())
												.send(RedisChannels.FRIEND_REMOVE_OUTBOUND.getChannelName(), jedis);
										Bukkit.getScheduler().runTaskLater(main, new Runnable() {
											@Override
											public void run() {
												try {
													if (stillLooking.contains(player.getName())) {
														stillLooking.remove(player.getName());
														main.getMySQL().saveFriendAccount(player.getUniqueId().toString());
														main.getMySQL().newRemoval(offlinePlayer.getUniqueId().toString(), player.getUniqueId().toString());
													}
												} catch (Exception e) {
													System.out.println("[NetworkTools] Unable to execute mysql operation");
												}
											}
										}, 10L);
									}
								} else {
									player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7" + name + " is not on your friends list!"));
								}
							}
						});
					}
				} else if (action.equalsIgnoreCase("tp")) {
					player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7Coming soon!"));
					//make ProxyTransport
				} else if (action.equalsIgnoreCase("accept")) {
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (main.getRequests().get(player.getUniqueId()) != null && main.getRequests().get(player.getUniqueId()).contains(target.getUniqueId().toString())) {
							Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + target.getName() + " &7is now a friend!"));
									target.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &d" + player.getName() + " &7is now a friend!"));
									main.getRequests().get(player.getUniqueId()).remove(target.getUniqueId().toString());
									main.getFriends().get(player.getUniqueId()).add(target.getUniqueId().toString());
									main.getFriends().get(target.getUniqueId()).add(player.getUniqueId().toString());
									try {
										main.getMySQL().saveFriendAccount(player.getUniqueId().toString());
										main.getMySQL().saveFriendAccount(target.getUniqueId().toString());
									} catch (Exception e) {
										System.out.println("[NetworkTools] Unable to execute mysql operation");
									}
								}
							});
						} else {
							player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You do not have a pending invite from " + name));
						}
					} else {
						player.sendMessage(StringUtils.msg("&d&lFRIENDS &5■ &7You do not have a pending invite from " + name));
					}
				} else {
					sendHelpMessage(player);
				}
			} else {
				sendHelpMessage(player);
			}
		}
		return true;
	}

	private void sendHelpMessage(Player player) {
		player.sendMessage(StringUtils.msg("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
		player.sendMessage(StringUtils.msg("&8» &d/friend &f- &7Bring up this help page"));
		player.sendMessage(StringUtils.msg("&8» &d/friend add PLAYER &f- &7Add a player as a friend"));
		player.sendMessage(StringUtils.msg("&8» &d/friend accept PLAYER &f- &7Add a player as a friend"));
		player.sendMessage(StringUtils.msg("&8» &d/friend deny PLAYER &f- &7Deny a friend request"));
		player.sendMessage(StringUtils.msg("&8» &d/friend remove PLAYER &f- &7Remove a friend"));
		player.sendMessage(StringUtils.msg("&8» &d/friend tp PLAYER &f- &7Teleport to a friends server"));
		player.sendMessage(StringUtils.msg("&8» &d/friend chat &f- &7Toggle friend chat on/off"));
		player.sendMessage(StringUtils.msg("&8» &d/friend requests &f- &7View friend requests"));
		player.sendMessage(StringUtils.msg("&8» &d/friend list &f- &7View all of your online/offline friends"));
		player.sendMessage(StringUtils.msg("&8» &d/friend settings &f- &7Bring up the settings menu"));
		player.sendMessage(StringUtils.msg("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
	}

	private boolean isOnline(String target) {
		Player t = Bukkit.getPlayer(target);
		return t != null;
	}

	private BaseComponent[] getHoverMessage(String message, String hoverMessage, String command) {
		BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
		BaseComponent[] hoverText = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', hoverMessage));
		ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
		for (BaseComponent component : components) {
			component.setClickEvent(clickEvent);
			component.setHoverEvent(hoverEvent);
		}
		return components;
	}

	public static List<String> getStillLooking() {
		return stillLooking;
	}

}
