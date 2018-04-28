package me.thesquadmc.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TSMCUser {
	
	private static final Map<UUID, TSMCUser> USERS = new HashMap<>();

	private final UUID player;
	private final String realname;

	private final List<UUID> friends = Lists.newArrayList();
	private final List<UUID> requests = Lists.newArrayList();
	private final List<Note> notes = Lists.newArrayList();
	
	private Map<PlayerSetting<?>, Object> settings = Maps.newHashMap();
	
	private String loginTime;
	private boolean vanished = false, ytVanished = false;
	private boolean xray, monitor = true, reports = true;
	private boolean forcefield = false, nicknamed = false;
	private String skinKey = "", signature = "";
	
	public TSMCUser(OfflinePlayer player) {
		Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null user");
		this.player = player.getUniqueId();
		this.realname = player.getName();
		this.xray = Bukkit.getServerName().toUpperCase().contains("FACTIONS");
	}
	
	public TSMCUser(UUID player) {
		Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null UUID");
		this.player = player;
		this.xray = Bukkit.getServerName().toUpperCase().contains("FACTIONS");
		this.realname = Bukkit.getOfflinePlayer(player).getName();
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(player);
	}
	
	public Player getPlayerOnline() {
		return Bukkit.getPlayer(player);
	}
	
	public void addFriend(OfflinePlayer friend) {
		Preconditions.checkNotNull(friend, "Cannot add null friend");
		this.friends.add(friend.getUniqueId());
	}
	
	public void addFriend(UUID friend) {
		Preconditions.checkNotNull(friend, "Cannot add null friend");
		this.friends.add(friend);
	}
	
	public boolean removeFriend(OfflinePlayer friend) {
		return friend != null && removeFriend(friend.getUniqueId());
	}
	
	public boolean removeFriend(UUID friend) {
		return friends.remove(friend);
	}
	
	public boolean isFriend(OfflinePlayer friend) {
		return friend != null && isFriend(friend.getUniqueId());
	}
	
	public boolean isFriend(UUID friend) {
		return friends.contains(friend);
	}
	
	public boolean hasFriends() {
		return !friends.isEmpty();
	}
	
	public List<UUID> getFriends() {
		return Collections.unmodifiableList(friends);
	}
	
	public void clearFriends() {
		this.friends.clear();
	}
	
	public void addRequest(OfflinePlayer request) {
		Preconditions.checkNotNull(request, "Cannot add null request");
		this.friends.add(request.getUniqueId());
	}
	
	public void addRequest(UUID request) {
		Preconditions.checkNotNull(request, "Cannot add null request");
		this.friends.add(request);
	}
	
	public boolean removeRequest(OfflinePlayer request) {
		return request != null && removeRequest(request.getUniqueId());
	}
	
	public boolean removeRequest(UUID request) {
		return friends.remove(request);
	}
	
	public boolean hasRequestFrom(OfflinePlayer request) {
		return request != null && hasRequestFrom(request.getUniqueId());
	}
	
	public boolean hasRequestFrom(UUID request) {
		return requests.contains(request);
	}
	
	public boolean hasRequests() {
		return !requests.isEmpty();
	}
	
	public List<UUID> getRequests() {
		return Collections.unmodifiableList(requests);
	}
	
	public void clearRequests() {
		this.requests.clear();
	}
	
	public <T> T updateSetting(PlayerSetting<T> setting, T value) {
		return setting.getSettingType().cast(settings.put(setting, value));
	}
	
	public void overrideSettings(Map<PlayerSetting<?>, Object> settings) {
		this.settings = settings;
	}
	
	public <T> T getSetting(PlayerSetting<T> setting) {
		return (setting != null) ? setting.getSettingType()
				.cast(settings.getOrDefault(setting, setting.getDefaultValue())) : null;
	}
	
	public void resetSettingsToDefault() {
		this.settings.clear();
		for (PlayerSetting<?> setting : PlayerSetting.values()) {
			this.settings.put(setting, setting.getDefaultValue());
		}
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void addNote(Note note) {
		notes.add(note);
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}
	
	public String getLoginTime() {
		return loginTime;
	}
	
	public void setVanished(boolean vanished) {
		this.vanished = vanished;
	}
	
	public boolean isVanished() {
		return vanished;
	}
	
	public void setYtVanished(boolean ytVanished) {
		this.ytVanished = ytVanished;
	}
	
	public boolean isYtVanished() {
		return ytVanished;
	}
	
	public void setXray(boolean xray) {
		this.xray = xray;
	}
	
	public boolean isXray() {
		return xray;
	}
	
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	
	public boolean hasMonitor() {
		return monitor;
	}
	
	public void setReports(boolean reports) {
		this.reports = reports;
	}
	
	public boolean showReports() {
		return reports;
	}
	
	public void setForcefield(boolean forcefield) {
		this.forcefield = forcefield;
	}
	
	public boolean hasForcefield() {
		return forcefield;
	}
	
	public void setNicknamed(boolean nicknamed) {
		this.nicknamed = nicknamed;
	}
	
	public boolean isNicknamed() {
		return this.nicknamed;
	}
	
	public void setSkinKey(String skinKey) {
		this.skinKey = skinKey;
	}
	
	public String getSkinKey() {
		return skinKey;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getRealname() {
		return realname;
	}
	
	public void clearLocalizedData() {
		this.friends.clear();
		this.requests.clear();
		this.settings.clear();
	}
	
	public static TSMCUser fromPlayer(OfflinePlayer player) {
		return (player != null) ? USERS.computeIfAbsent(player.getUniqueId(), TSMCUser::new) : null;
	}
	
	public static TSMCUser fromUUID(UUID player) {
		return (player != null) ? USERS.computeIfAbsent(player, TSMCUser::new) : null;
	}
	
	public static boolean isLoaded(OfflinePlayer player) {
		return player != null && USERS.containsKey(player.getUniqueId());
	}
	
	public static boolean isLoaded(UUID player) {
		return USERS.containsKey(player);
	}
	
	public static void unloadUser(TSMCUser user) {
		USERS.remove(user.player);
	}
	
	public static void unloadUser(OfflinePlayer player) {
		if (player == null) return;
		USERS.remove(player.getUniqueId());
	}
	
	public static void unloadUser(UUID player) {
		USERS.remove(player);
	}
	
	public static Collection<TSMCUser> getUsers() {
		return Collections.unmodifiableCollection(USERS.values());
	}
	
	public static void clearUsers() {
		USERS.clear();
	}

	public static TSMCUser fromDocument(Document document) {
		//TSMCUser user = new TSMCUser(document.)
		throw new UnsupportedOperationException("Not available in this version");
	}
	
}