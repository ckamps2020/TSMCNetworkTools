package me.thesquadmc.managers;

import java.util.HashMap;
import java.util.Map;

import me.thesquadmc.abstraction.HumanNPC;
import me.thesquadmc.abstraction.MobNPC;

public final class NPCManager {

	private Map<String, MobNPC> npcs = new HashMap<>();
	private Map<String, HumanNPC> humanNpcs = new HashMap<>();

	public boolean isHumanNpcNameTaken(String name) {
		return humanNpcs.containsKey(name);
	}

	public boolean isNpcNameTaken(String name) {
		return npcs.containsKey(name);
	}

	public HumanNPC getHumanNPC(String name) {
		return humanNpcs.get(name);
	}

	public MobNPC getMobNPC(String name) {
		return npcs.get(name);
	}

	public Map<String, MobNPC> getNpcs() {
		return npcs;
	}

	public Map<String, HumanNPC> getHumanNpcs() {
		return humanNpcs;
	}

}