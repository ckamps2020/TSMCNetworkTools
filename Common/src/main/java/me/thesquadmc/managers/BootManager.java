package me.thesquadmc.managers;

import me.thesquadmc.utils.file.FileUtils;

import java.io.File;

public final class BootManager {

	public void bootBedwars() {
		try {
			FileUtils.deleteFile(new File(FileUtils.getRootFolder() + File.separator + "plugins" + File.separator + "TSMCBedwars"));
			FileUtils.deleteFile(new File(FileUtils.getRootFolder() + File.separator + "maps"));
			org.apache.commons.io.FileUtils.copyDirectory(new File("shared" + File.separator + "bedwars" + File.separator + "TSMCBedwars"), new File(FileUtils.getRootFolder() + File.separator + "plugins" + File.separator + "TSMCBedwars"));
			org.apache.commons.io.FileUtils.copyDirectory(new File("shared" + File.separator + "bedwars" + File.separator + "maps"), new File(FileUtils.getRootFolder() + File.separator + "maps"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
