package com.thesquadmc.networktools.objects;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Party {

    private final Map<UUID, Long> members;
    private transient boolean destroyed = false;
    private UUID owner;

    public Party(UUID owner, UUID... members) {
        this.owner = owner;
        this.members = Maps.newHashMapWithExpectedSize(members.length);

        long now = System.currentTimeMillis();
        for (UUID member : members) {
            if (member == null) continue;
            this.members.put(member, now);
        }
    }

    public Party(OfflinePlayer owner, OfflinePlayer... members) {
        this.owner = owner.getUniqueId();
        this.members = Maps.newHashMapWithExpectedSize(members.length);

        long now = System.currentTimeMillis();
        for (OfflinePlayer member : members) {
            if (member == null) continue;
            this.members.put(member.getUniqueId(), now);
        }
    }

    public void setOwner(OfflinePlayer owner) {
        this.setOwner(owner.getUniqueId());
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(owner);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.members.remove(owner);
    }

    public boolean isOwner(OfflinePlayer player) {
        return player != null && player.getUniqueId().equals(owner);
    }

    public boolean isOwner(UUID playerUUID) {
        return owner.equals(playerUUID);
    }

    public void addMember(OfflinePlayer member, long timeJoined) {
        if (member == null) return;
        this.members.put(member.getUniqueId(), timeJoined);
    }

    public void addMember(OfflinePlayer member) {
        this.addMember(member, System.currentTimeMillis());
    }

    public void removeMember(OfflinePlayer member) {
        if (member == null) return;
        this.members.remove(member.getUniqueId());
    }

    public boolean isMember(OfflinePlayer member) {
        return member != null && members.containsKey(member.getUniqueId());
    }

    public boolean isMember(UUID memberUUID) {
        return members.containsKey(memberUUID);
    }

    public Set<OfflinePlayer> getMembers() {
        return members.keySet().stream()
                .map(Bukkit::getOfflinePlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public int getMemberCount(boolean includeOwner) {
        return members.size() + (includeOwner ? 1 : 0);
    }

    public int getMemberCount() {
        return getMemberCount(true);
    }

    public long getMemberLastActivity(OfflinePlayer member) {
        return members.compute(member.getUniqueId(), (k, v) -> member.isOnline() ? System.currentTimeMillis() : v);
    }

    public void clearMembers() {
        this.members.clear();
    }

    public void destroy() {
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public int hashCode() {
        return 31 * ((owner == null ? 0 : owner.hashCode()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Party)) return false;

        Party other = (Party) obj;
        return Objects.equals(owner, other.owner);
    }

}