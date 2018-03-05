package me.thesquadmc.managers;

import java.util.ArrayList;
import java.util.List;

public final class QueueManager {

	private List<String> soloBW = new ArrayList<>();

	public String getFirstSoloBW() {
		if (!soloBW.isEmpty()) {
			return soloBW.get(0);
		}
		return "N/A";
	}

	public List<String> getSoloBW() {
		return soloBW;
	}

}
