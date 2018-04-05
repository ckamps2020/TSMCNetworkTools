package me.thesquadmc.managers;

import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.objects.TempData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Legacy API. See {@link TSMCUser}
 */
@Deprecated
public final class TempDataManager {

	private Map<UUID, TempData> allTempData = new HashMap<>();

	public boolean hasTempData(UUID uuid) {
		return allTempData.get(uuid) != null;
	}

	public TempData getTempData(UUID uuid) {
		return allTempData.get(uuid);
	}

	public void unregisterNewData(UUID uuid) {
		allTempData.remove(uuid);
	}

	public void registerNewData(UUID uuid, TempData tempData) {
		allTempData.put(uuid, tempData);
	}

	public Map<UUID, TempData> getAllTempData() {
		return allTempData;
	}

}
