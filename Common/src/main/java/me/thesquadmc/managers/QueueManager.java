package me.thesquadmc.managers;

import java.util.ArrayList;
import java.util.List;

public final class QueueManager {

	private List<String> soloBW = new ArrayList<>();
	private List<String> soloStandby = new ArrayList<>();
	private List<String> priority = new ArrayList<>();

	public String getFirstSoloBW() {
		if (!priority.isEmpty()) {
			return priority.get(0);
		}
		if (!soloBW.isEmpty()) {
			return soloBW.get(0);
		}
		return "N/A";
	}

	public List<String> getSoloStandby() {
		return soloStandby;
	}

	public List<String> getPriority() {
		return priority;
	}

	public List<String> getSoloBW() {
		return soloBW;
	}

}
