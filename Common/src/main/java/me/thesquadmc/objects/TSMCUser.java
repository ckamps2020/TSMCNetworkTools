package me.thesquadmc.objects;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Preconditions;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.thesquadmc.objects.logging.Note;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static me.thesquadmc.networking.mongo.UserDatabase.*;

public class TSMCUser {

    private static final Map<UUID, TSMCUser> USERS = new HashMap<>();

    private final UUID uuid;
    private String name;

    private final Set<String> previousNames = Sets.newHashSet();
    private final Set<UUID> friends = Sets.newHashSet();
    private final Set<UUID> requests = Sets.newHashSet();
    private final Set<Note> notes = Sets.newHashSet();

    private Map<PlayerSetting<?>, Object> settings = Maps.newHashMap();

    private long loginTime;
    private boolean vanished = false, ytVanished = false;
    private boolean xray, monitor = true, reports = true;
    private boolean forcefield = false, nicknamed = false;
    private String skinKey = "", signature = "";

    public TSMCUser(OfflinePlayer player) {
        Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null user");
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.xray = Bukkit.getServerName().toUpperCase().contains("FACTIONS");
    }

    public TSMCUser(UUID player) {
        Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null UUID");
        this.uuid = player;
        this.xray = Bukkit.getServerName().toUpperCase().contains("FACTIONS");
        this.name = Bukkit.getOfflinePlayer(player).getName();
    }

    public TSMCUser(UUID player, String name) {
        Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null UUID");
        this.uuid = player;
        this.xray = Bukkit.getServerName().toUpperCase().contains("FACTIONS");
        this.name = name;
    }

    public java.util.UUID getUuid() {
        return uuid;
    }


    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public Player getPlayerOnline() {
        return Bukkit.getPlayer(uuid);
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

    public Set<UUID> getFriends() {
        return Collections.unmodifiableSet(friends);
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

    public Set<UUID> getRequests() {
        return Collections.unmodifiableSet(requests);
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

    public Set<Note> getNotes() {
        return Collections.unmodifiableSet(notes);
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public Set<String> getPreviousNames() {
        return previousNames;
    }

    public void addPreviousName(String name) {
        previousNames.add(name);
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getLoginTime() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void clearLocalizedData() {
        this.friends.clear();
        this.requests.clear();
        this.settings.clear();
        this.notes.clear();
    }

    public static TSMCUser fromPlayer(OfflinePlayer player) {
        return (player != null) ? USERS.computeIfAbsent(player.getUniqueId(), TSMCUser::new) : null;
    }

    public static TSMCUser fromUUID(UUID player) {
        return (player != null) ? USERS.computeIfAbsent(player, TSMCUser::new) : null;
    }

    public static void loadUser(TSMCUser user) {
        Preconditions.checkNotNull(user, "User cannot be null");
        USERS.put(user.getUuid(), user);
    }

    public static boolean isLoaded(OfflinePlayer player) {
        return player != null && USERS.containsKey(player.getUniqueId());
    }

    public static boolean isLoaded(UUID player) {
        return USERS.containsKey(player);
    }

    public static void unloadUser(TSMCUser user) {
        USERS.remove(user.uuid);
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
        TSMCUser user = new TSMCUser(document.get("_id", UUID.class), document.getString("name"));

        Set<String> previousNames = (Set<String>) document.get(PREVIOUS_NAMES, Set.class);
        if (previousNames != null) {
            previousNames.addAll(user.previousNames);
        }

        Set<UUID> friends = (Set<UUID>) document.get(FRIENDS, Set.class);
        if (friends != null) {
            friends.addAll(user.friends);
        }

        Set<UUID> requests = (Set<UUID>) document.get(REQUESTS, Set.class);
        if (requests != null) {
            requests.addAll(user.requests);
        }

        Set<Document> notes = (Set<Document>) document.get(NOTES, Set.class);
        if (notes != null) {
            notes.stream().map(Note::fromDocument).collect(Collectors.toList()).addAll(user.notes);
        }

        user.vanished = document.getBoolean(VANISHED, false);
        user.ytVanished = document.getBoolean(YT_VANISHED, false);
        user.xray = document.getBoolean(XRAY, false);
        user.monitor = document.getBoolean(MONITOR, false);
        user.reports = document.getBoolean(REPORTS, false);
        user.forcefield = document.getBoolean(FORCEFIELD, false);
        user.nicknamed = document.getBoolean(NICKNAMED, false);
        user.skinKey = document.getString(SKIN_KEY);
        user.signature = document.getString(SIGNATURE);

        loadUser(user);
        return user;
    }

    public static Document toDocument(TSMCUser user) {
        return new Document("_id", user.uuid)
                .append(NAME, user.name)

                .append(FRIENDS, user.friends)
                .append(REQUESTS, user.requests)
                .append(NOTES, user.notes)

                .append(VANISHED, user.vanished)
                .append(YT_VANISHED, user.ytVanished)
                .append(XRAY, user.xray)
                .append(MONITOR, user.monitor)
                .append(REPORTS, user.reports)
                .append(FORCEFIELD, user.forcefield)
                .append(NICKNAMED, user.nicknamed)

                .append(SKIN_KEY, user.skinKey)
                .append(SIGNATURE, user.signature);
    }

}