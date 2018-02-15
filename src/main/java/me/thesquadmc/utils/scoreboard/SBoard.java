package me.thesquadmc.utils.scoreboard;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class SBoard {

	private Scoreboard scoreboard;
	private Objective objective;
	private BiMap<String, SBoardEntry> entries;

	private int teamId;

	public SBoard(String title) {
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		if (scoreboard.getObjective("spig-" + title) != null) {
			scoreboard.getObjective("spig-" + title).unregister();
		}
		this.objective = scoreboard.registerNewObjective("spig-" + title, "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		setTitle(title);

		this.entries = HashBiMap.create();
		this.teamId = 1;
	}

	public SBoard(String title, Scoreboard sb) {
		this.scoreboard = sb;
		if (scoreboard.getObjective("spig-" + title) != null) {
			scoreboard.getObjective("spig-" + title).unregister();
		}
		this.objective = scoreboard.registerNewObjective("spig-" + title, "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		setTitle(title);

		this.entries = HashBiMap.create();
		this.teamId = 1;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setTitle(String title) {
		objective.setDisplayName(title);
	}

	public BiMap<String, SBoardEntry> getEntries() {
		return HashBiMap.create(entries);
	}

	public SBoardEntry getEntry(String key) {
		return entries.get(key);
	}

	public SBoardEntry add(String name, int value) {
		return add((String) null, name, value, true);
	}

	public SBoardEntry add(Enum key, String name, int value) {
		return add(key.name(), name, value);
	}

	public SBoardEntry add(String key, String name, int value) {
		return add(key, name, value, false);
	}

	public SBoardEntry add(Enum key, String name, int value, boolean overwrite) {
		return add(key.name(), name, value, overwrite);
	}

	public SBoardEntry add(String key, String name, int value, boolean overwrite) {
		if (key == null && !contains(name)) {
			throw new IllegalArgumentException("Entry could not be found with the supplied name and no key was supplied");
		}

		if (overwrite && contains(name)) {
			SBoardEntry entry = getEntryByName(name);
			if (key != null && entries.get(key) != entry) {
				throw new IllegalArgumentException("Supplied key references a different score than the one to be overwritten");
			}

			entry.setValue(value);
			return entry;
		}

		if (entries.get(key) != null) {
			throw new IllegalArgumentException("Score already exists with that key");
		}

		int count = 0;
		String origName = name;
		if (!overwrite) {
			Map<Integer, String> created = create(name);
			for (Map.Entry<Integer, String> entry : created.entrySet()) {
				count = entry.getKey();
				name = entry.getValue();
			}
		}

		SBoardEntry entry = new SBoardEntry(key, this, value, origName, count);
		entry.update(name);
		entries.put(key, entry);
		return entry;
	}

	public void remove(String key) {
		remove(getEntry(key));
	}

	public void remove(SBoardEntry entry) {
		if (entry.getSBoard() != this) {
			throw new IllegalArgumentException("Supplied entry does not belong to this SBoard");
		}
		String key = entries.inverse().get(entry);
		if (key != null) {
			entries.remove(key);
		}
		entry.remove();
	}

	private Map<Integer, String> create(String name) {
		int count = 0;
		while (contains(name)) {
			name = ChatColor.RESET + name;
			count++;
		}
		if (name.length() > 48) {
			name = name.substring(0, 47);
		}
		if (contains(name)) {
			throw new IllegalArgumentException("Could not find a suitable replacement name for '" + name + "'");
		}
		Map<Integer, String> created = new HashMap<>();
		created.put(count, name);
		return created;
	}

	public int getTeamId() {
		return teamId++;
	}

	public SBoardEntry getEntryByName(String name) {
		for (SBoardEntry entry : entries.values()) {
			if (entry.getName().equals(name)) {
				return entry;
			}
		}
		return null;
	}

	public boolean contains(String name) {
		for (SBoardEntry entry : entries.values()) {
			if (entry.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public void add(Player player) {
		player.setScoreboard(scoreboard);
	}

}
