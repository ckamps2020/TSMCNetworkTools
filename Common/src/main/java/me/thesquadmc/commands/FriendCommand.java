package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.inventories.SettingsMenu;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			TSMCUser user = TSMCUser.fromPlayer(player);
			
			if (args.length == 0) {
				sendHelpMessage(player);
			} else if (args.length == 1) {
				String action = args[0];
				if (action.equalsIgnoreCase("requests")) {
					if (user.hasRequests()) {
						player.sendMessage(CC.translate("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
						for (UUID requesterUUID : user.getRequests()) {
							Player requester = Bukkit.getPlayer(requesterUUID);
							if (requester != null) {
								player.spigot().sendMessage(new ComponentBuilder(CC.translate("&8[&a✔&8]"))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requester.getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(CC.translate("&dClick to accept the friend request"))))
										.append(CC.translate(" &8[&c✖&8]"))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + requester.getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(CC.translate("&cClick to deny the friend request"))))
										.append(CC.translate(" &7" + requester.getName()))
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(CC.translate("&7Pending friend request"))))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests"))
										.create());
							}
						}
						player.sendMessage(CC.translate("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
					} else {
						player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have no pending friend requests!"));
					}
				} else if (action.equalsIgnoreCase("chat")) {
					Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
						@Override
						public void run() {
							if (user.getSetting(PlayerSetting.FRIEND_CHAT)) {
								user.updateSetting(PlayerSetting.FRIEND_CHAT, false);
								player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have toggled friend chat mode &doff"));
							} else {
								user.updateSetting(PlayerSetting.FRIEND_CHAT, true);
								player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have toggled friend chat mode &don"));
							}
						}
					});
				} else if (action.equalsIgnoreCase("list")) {
					if (user.hasFriends()) {
						if (!online.containsKey(player.getUniqueId())) {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7Fetching friends list..."));
							Bukkit.getScheduler().runTaskLater(main, new Runnable() {
								@Override
								public void run() {
									player.sendMessage(CC.translate("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
									for (UUID friendUUID : user.getFriends()) {
										OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(friendUUID);
										if (offlinePlayer == null) {
											continue;
										}
										
										player.sendMessage(CC.translate("&7" + offlinePlayer.getName()));
									}
									if (online.containsKey(player.getUniqueId())) {
										online.remove(player.getUniqueId());
									}
									player.sendMessage(CC.translate("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
								}
							}, 1L);
						} else {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You are currently listing friends, please standby..."));
						}
					} else {
						player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have no friends! Get started by adding some!"));
					}
				} else if (action.equalsIgnoreCase("settings")) {
					SettingsMenu.buildSettingsMenu(player);
				} else if (action.equalsIgnoreCase("chatsocialspy")) {
					if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
						if (user.getSetting(PlayerSetting.SOCIALSPY)) {
							user.updateSetting(PlayerSetting.SOCIALSPY, false);
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have toggled friend social spy &doff"));
						} else {
							user.updateSetting(PlayerSetting.SOCIALSPY, true);
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have toggled friend social spy &don"));
						}
					} else {
						player.sendMessage(CC.translate("&cYou do not have permission to use this command!"));
					}
				} else {
					sendHelpMessage(player);
				}
			} else if (args.length == 2) {
				String action = args[0];
				String name = args[1];
				if (action.equalsIgnoreCase("add")) {
					if (user.isFriend(Bukkit.getOfflinePlayer(name))) {
						player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7That person is already on your friends list!"));
						return true;
					}
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (target.getUniqueId() == player.getUniqueId()) {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You can't add yourself as a friend silly!"));
							return true;
						}
						if (!user.getSetting(PlayerSetting.FRIEND_REQUESTS)) {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7" + target.getName() + " currently has their friend requests disabled!"));
							return true;
						}
						TSMCUser targetUser = TSMCUser.fromPlayer(target);
						if (!targetUser.hasRequestFrom(player)) {
							targetUser.addRequest(player);
							target.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player.getName() + " &7has sent you a friend request"));
							target.spigot().sendMessage(new ComponentBuilder(CC.translate("&a&l[ACCEPT]"))
									.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName()))
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(CC.translate("&dClick to accept the friend request"))))
									.append(CC.translate(" &c&l[DECLINE]"))
									.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName()))
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(CC.translate("&cClick to deny the friend request"))))
									.create());
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7A friend request has been sent to &d" + target.getName()));
							Bukkit.getScheduler().runTaskLaterAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									if (targetUser.hasRequestFrom(player)) {
										targetUser.removeRequest(player);
										target.sendMessage(CC.translate("&d&lFRIENDS &5■ &7Your friend request from " + player.getName() + " has expired"));
										if (isOnline(player.getName())) {
											player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You friend request to " + target.getName() + " has expired"));
										}
									} else if (user.isFriend(target)) {
										player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You friend request to " + target.getName() + " has expired"));
									}
								}
							}, 300 * 20L);
						} else {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have already sent " + target.getName() + " a request in the last 5 minutes!"));
						}
					} else {
						player.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + name + " &7is not online or does not exist!"));
					}
				} else if (action.equalsIgnoreCase("deny")) {
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (user.hasRequestFrom(target)) {
							user.removeRequest(target);
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You have declined &d" + target.getName() + "’s &7friend request"));
							target.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player.getName() + " &7has declined your friend request"));
						} else {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You do not have a pending invite from " + target.getName()));
						}
					} else {
						player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You do not have a pending invite from " + name));
					}
				} else if (action.equalsIgnoreCase("remove")) {
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (user.isFriend(target)) {
							user.removeFriend(target);
							TSMCUser.fromPlayer(target).removeFriend(player);
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + target.getName() + " &7is no longer a friend"));
							target.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player.getName() + " &7has removed you as a friend"));
							Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									Multithreading.runAsync(new Runnable() {
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
								}
							});
						} else {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + name + " &7is not on your friends list!"));
						}
					} else {
						Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
							@Override
							public void run() {
								OfflinePlayer target = Bukkit.getOfflinePlayer(name);
								if (user.isFriend(target)) {
									user.removeFriend(target);
									player.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + name + " &7is no longer a friend"));
									Multithreading.runAsync(new Runnable() {
										@Override
										public void run() {
											try {
												main.getMySQL().saveFriendAccount(player.getUniqueId().toString());
												main.getMySQL().newRemoval(target.getUniqueId().toString(), player.getUniqueId().toString());
											} catch (Exception e) {
												System.out.println("[NetworkTools] Unable to execute mysql operation");
											}
										}
									});
								} else {
									player.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + name + " is not on your friends list!"));
								}
							}
						});
					}
				} else if (action.equalsIgnoreCase("tp")) {
					player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7Coming soon!"));
					//make ProxyTransport
				} else if (action.equalsIgnoreCase("accept")) {
					if (isOnline(name)) {
						Player target = Bukkit.getPlayer(name);
						if (user.hasRequestFrom(target)) {
							Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									player.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + target.getName() + " &7is now a friend!"));
									target.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player.getName() + " &7is now a friend!"));
									user.removeRequest(target);
									user.addFriend(target);
									TSMCUser.fromPlayer(target).addFriend(player);
									Multithreading.runAsync(new Runnable() {
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
								}
							});
						} else {
							player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You do not have a pending invite from " + name));
						}
					} else {
						player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7You do not have a pending invite from " + name));
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
		player.sendMessage(CC.translate("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
		player.sendMessage(CC.translate("&8» &d/friend &f- &7Bring up this help page"));
		player.sendMessage(CC.translate("&8» &d/friend add PLAYER &f- &7Add a player as a friend"));
		player.sendMessage(CC.translate("&8» &d/friend accept PLAYER &f- &7Add a player as a friend"));
		player.sendMessage(CC.translate("&8» &d/friend deny PLAYER &f- &7Deny a friend request"));
		player.sendMessage(CC.translate("&8» &d/friend remove PLAYER &f- &7Remove a friend"));
		player.sendMessage(CC.translate("&8» &d/friend tp PLAYER &f- &7Teleport to a friends server"));
		player.sendMessage(CC.translate("&8» &d/friend chat &f- &7Toggle friend chat on/off"));
		player.sendMessage(CC.translate("&8» &d/friend requests &f- &7View friend requests"));
		player.sendMessage(CC.translate("&8» &d/friend list &f- &7View all of your online/offline friends"));
		player.sendMessage(CC.translate("&8» &d/friend settings &f- &7Bring up the settings menu"));
		player.sendMessage(CC.translate("&8&m---------------&8[ &d&lFRIENDS &8]&8&m---------------"));
	}

	private boolean isOnline(String target) {
		Player t = Bukkit.getPlayer(target);
		return t != null;
	}
	public static List<String> getStillLooking() {
		return stillLooking;
	}

}
