package com.thesquadmc.managers;

import com.thesquadmc.objects.Party;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PartyManager {
	
	private final Set<Party> parties = new HashSet<>();
	
	public Party createParty(OfflinePlayer owner, OfflinePlayer... members) {
		Party party = new Party(owner, members);
		this.parties.add(party);
		return party;
	}
	
	public Party createParty(UUID owner, UUID... members) {
		Party party = new Party(owner, members);
		this.parties.add(party);
		return party;
	}
	
	public void addParty(Party party) {
		this.parties.add(party);
	}
	
	public boolean removeParty(Party party) {
		return parties.remove(party);
	}
	
	public Party getOwnedParty(OfflinePlayer owner) {
		return (owner != null) ? getOwnedParty(owner.getUniqueId()) : null;
	}
	
	public Party getOwnedParty(UUID owner) {
		for (Party party : parties)
			if (party.isOwner(owner)) return party;
		return null;
	}
	
	public Party getParty(OfflinePlayer member) {
		return member != null ? getParty(member.getUniqueId()) : null;
	}
	
	public Party getParty(UUID member) {
		for (Party party : parties)
			if (party.isOwner(member) || party.isMember(member)) return party;
		return null;
	}
	
	public boolean ownsParty(OfflinePlayer owner) {
		return getOwnedParty(owner) != null;
	}
	
	public boolean ownsParty(UUID owner) {
		return getOwnedParty(owner) != null;
	}
	
	public boolean hasParty(OfflinePlayer member) {
		return getParty(member) != null;
	}
	
	public boolean hasParty(UUID member) {
		return getParty(member) != null;
	}
	
	public void clearParties() {
		this.parties.clear();
	}
	
}