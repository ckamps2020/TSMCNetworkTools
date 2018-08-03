package com.thesquadmc.networktools.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.objects.logging.IPInfo;
import com.thesquadmc.networktools.objects.logging.Note;
import com.thesquadmc.networktools.player.stats.ServerStatistics;
import com.thesquadmc.networktools.utils.DocumentUtils;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.ServerType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.thesquadmc.networktools.networking.mongo.UserDatabase.FRIENDS;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.IPS;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.LAST_MESSAGER;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.NAME;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.NICKNAME;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.NOTES;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.PREVIOUS_NAMES;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.REQUESTS;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.SETTINGS;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.SIGNATURE;
import static com.thesquadmc.networktools.networking.mongo.UserDatabase.SKIN_KEY;

public class TSMCUser {

    private static final Map<UUID, TSMCUser> USERS = new HashMap<>();

    /**
     * The UUID of the player
     **/
    private final UUID uuid;

    /**
     * The player's current name
     **/
    private String name;

    /**
     * All previous names the player has had
     **/
    private final Set<String> previousNames = Sets.newHashSet();

    /**
     * A list of {@link IPInfo} that this player has
     */
    private final Set<IPInfo> ips = Sets.newHashSet();

    /**
     * Per server statistics for the player
     */
    private Map<String, ServerStatistics> serverStats = Maps.newHashMap();

    /**
     * A list of friends that this player has
     */
    private final Set<UUID> friends = Sets.newHashSet();

    /**
     * A list of friend requests this player has
     */
    private final Set<UUID> requests = Sets.newHashSet();

    /**
     * A list of players this player has ignored
     */
    private final Set<UUID> ignored = Sets.newHashSet();

    /**
     * A list of {@link Note} that this player has
     */
    private final Set<Note> notes = Sets.newHashSet();

    /**
     * The player's current nickname
     **/
    private String nickname;

    /**
     * The player's settings
     */
    private Map<PlayerSetting<?>, Object> settings = Maps.newHashMap();

    /**
     * The last player to send this player a message
     * Used in the /reply command
     */
    private UUID lastMessager;

    private String skinKey = "", signature = "";

    public TSMCUser(OfflinePlayer player) {
        Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null user");
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public TSMCUser(UUID player) {
        Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null UUID");
        this.uuid = player;
        this.name = Bukkit.getOfflinePlayer(player).getName();
    }

    public TSMCUser(UUID player, String name) {
        Preconditions.checkNotNull(player, "Cannot construct a TSMCUser from a null UUID");
        this.uuid = player;
        this.name = name;
    }

    public static void loadUser(TSMCUser user) {
        Preconditions.checkNotNull(user, "User cannot be null");
        USERS.put(user.getUUID(), user);
    }

    public static void unloadUser(TSMCUser user, boolean save) {
        USERS.remove(user.uuid);

        if (save) {
            NetworkTools.getInstance().getUserDatabase().saveUser(user);
        }
    }

    public static TSMCUser fromDocument(Document document) {
        TSMCUser user = new TSMCUser(document.get("_id", UUID.class), document.getString("name"));

        Set<String> previousNames = DocumentUtils.documentToStringSet(document, PREVIOUS_NAMES);
        previousNames.addAll(user.previousNames);

        Set<UUID> friends = DocumentUtils.documentToUUIDSet(document, FRIENDS);
        friends.addAll(user.friends);

        Set<UUID> requests = DocumentUtils.documentToUUIDSet(document, REQUESTS);
        if (requests != null) {
            requests.addAll(user.requests);
        }

        List<Document> notes = (List<Document>) document.get(NOTES);
        if (notes != null) {
            user.notes.addAll(notes.stream().map(Note::fromDocument).collect(Collectors.toList()));
        }

        List<Document> ips = (List<Document>) document.get(IPS);
        if (ips != null) {
            user.ips.addAll(ips.stream().map(IPInfo::fromDocument).collect(Collectors.toList()));
        }

        user.serverStats = (Map<String, ServerStatistics>) document.get("server_stats");
        System.out.println("server stats" + user.serverStats);

        user.nickname = document.getString(NICKNAME);
        user.skinKey = document.getString(SKIN_KEY);
        user.signature = document.getString(SIGNATURE);
        user.lastMessager = document.get(LAST_MESSAGER, UUID.class);

        Document settingsDocument = (Document) document.get(SETTINGS);
        for (PlayerSetting<?> setting : NetworkTools.getInstance().getPlayerSettings()) {
            if (settingsDocument.containsKey(setting.getName())) {
                setting.getSettingType().cast(user.settings.put(setting, settingsDocument.get(setting.getName(), setting.getSettingType())));
            }
        }

        loadUser(user);
        return user;
    }

    public static TSMCUser fromPlayer(OfflinePlayer player) {
        return (player != null) ? USERS.computeIfAbsent(player.getUniqueId(), TSMCUser::new) : null;
    }

    public static TSMCUser fromUUID(UUID player) {
        return (player != null) ? USERS.computeIfAbsent(player, TSMCUser::new) : null;
    }

    public static Document toDocument(TSMCUser user) {
        Map<String, Object> settings = Maps.newHashMapWithExpectedSize(user.settings.size());
        user.settings.forEach((setting, object) -> settings.put(setting.getName(), object));

        return new Document("_id", user.uuid)
                .append(NAME, user.name)
                .append(NICKNAME, user.nickname)

                .append(PREVIOUS_NAMES, user.previousNames)
                .append(FRIENDS, user.friends)
                .append(IPS, user.ips)
                .append(REQUESTS, user.requests)
                .append(NOTES, user.notes)
                .append(SETTINGS, settings)
                .append("server_stats", user.serverStats)

                .append(SKIN_KEY, user.skinKey)
                .append(SIGNATURE, user.signature)

                .append(LAST_MESSAGER, user.lastMessager);
    }

    public static boolean isLoaded(OfflinePlayer player) {
        return player != null && USERS.containsKey(player.getUniqueId());
    }

    public static boolean isLoaded(UUID player) {
        return USERS.containsKey(player);
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

    public ServerStatistics getServerStatistic(String server) {
        return serverStats.get(server);
    }

    public void addServerStatistic(ServerStatistics statistics) {
        serverStats.put(statistics.getServerName(), statistics);
    }

    public Set<ServerStatistics> getServerStatistics(ServerType type) {
        return serverStats.values().stream()
                .filter(statistics -> statistics.getType() == type)
                .collect(Collectors.toSet());
    }

    public void addIgnoredPlayer(UUID uuid) {
        ignored.add(uuid);
    }

    public void removeIgnoredPlayer(UUID uuid) {
        ignored.remove(uuid);
    }

    public boolean isIgnored(UUID uuid) {
        return ignored.contains(uuid);
    }

    public Set<UUID> getIgnoredPlayers() {
        return Collections.unmodifiableSet(ignored);
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

    public UUID getLastMessager() {
        return lastMessager;
    }

    public void setLastMessager(UUID lastMessager) {
        this.lastMessager = lastMessager;
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
        for (PlayerSetting<?> setting : NetworkTools.getInstance().getPlayerSettings()) {
            this.settings.put(setting, setting.getDefaultValue());
        }
    }

    public void setNickname(String nickname, boolean refresh) {
        this.nickname = nickname;

        if (refresh) {
            PlayerUtils.refreshPlayer(getPlayerOnline());
        }
    }

    public void unsetNickname(boolean refresh) {
        this.setNickname(name, refresh);
    }

    public void unsetNickname() {
        this.unsetNickname(true);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        PlayerUtils.setName(getPlayerOnline(), nickname);

        this.setNickname(nickname, true);
    }

    public boolean isNicknamed() {
        if (getNickname() == null || getNickname().isEmpty() || getNickname().equalsIgnoreCase(name)) {
            return false; //didn't know I needed this but oh well
        }

        return !name.equals(getNickname());
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

    public String getSkinKey() {
        return skinKey;
    }

    public void setSkinKey(String skinKey) {
        this.skinKey = skinKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public java.util.UUID getUUID() {
        return uuid;
    }

    public Set<IPInfo> getIPs() {
        return Collections.unmodifiableSet(ips);
    }

    public void addIP(String ip) {
        Optional<IPInfo> optional = ips.stream()
                .filter(info1 -> info1.getIP().equals(ip))
                .findFirst();

        if (optional.isPresent()) {
            IPInfo ipInfo = optional.get();

            ipInfo.setLastJoined(new Date());
            ipInfo.setCount(ipInfo.getCount() + 1);

        } else {
            ips.add(new IPInfo(ip, new Date(), new Date()));
        }
    }

}