package me.thesquadmc.utils.handlers;

import me.thesquadmc.utils.enums.UpdateType;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateHandler implements Runnable {

	private final JavaPlugin plugin;

	public UpdateHandler(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
	}

	public void run() {
		UpdateType[] arrayOfUpdateType;
		int j = (arrayOfUpdateType = UpdateType.values()).length;
		for (int i = 0; i < j; i++) {
			UpdateType updateType = arrayOfUpdateType[i];
			if (updateType.elapsed()) {
				this.plugin.getServer().getPluginManager().callEvent(new UpdateEvent(updateType));
			}
		}
	}

}
