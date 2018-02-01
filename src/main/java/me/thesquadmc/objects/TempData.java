package me.thesquadmc.objects;

import me.thesquadmc.utils.MessageSettings;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;

public final class TempData {

	private String loginTime;
	private MessageSettings staffchatSetting;
	private boolean staffchatEnabled;
	private MessageSettings adminchatSetting;
	private boolean adminchatEnabled;
	private MessageSettings managerSetting;
	private boolean managerchatEnabled;
	private boolean vanished;
	private boolean xray;
	private boolean forcefieldEnabled;
	private boolean ytVanishEnabled;

	public TempData() {
		loginTime = StringUtils.getDate();
		staffchatSetting = MessageSettings.GLOBAL;
		staffchatEnabled = true;
		adminchatSetting = MessageSettings.LOCAL;
		adminchatEnabled = true;
		managerSetting = MessageSettings.LOCAL;
		managerchatEnabled = true;
		vanished = false;
		forcefieldEnabled = false;
		ytVanishEnabled = false;
		if (Bukkit.getServerName().toUpperCase().contains("FACTIONS")) {
			xray = true;
		} else {
			xray = false;
		}
	}

	public boolean isYtVanishEnabled() {
		return ytVanishEnabled;
	}

	public void setYtVanishEnabled(boolean ytVanishEnabled) {
		this.ytVanishEnabled = ytVanishEnabled;
	}

	public boolean isForcefieldEnabled() {
		return forcefieldEnabled;
	}

	public void setForcefieldEnabled(boolean forcefieldEnabled) {
		this.forcefieldEnabled = forcefieldEnabled;
	}

	public boolean isXray() {
		return xray;
	}

	public void setXray(boolean xray) {
		this.xray = xray;
	}

	public boolean isVanished() {
		return vanished;
	}

	public void setVanished(boolean vanished) {
		this.vanished = vanished;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public MessageSettings getManagerSetting() {
		return managerSetting;
	}

	public void setManagerSetting(MessageSettings managerSetting) {
		this.managerSetting = managerSetting;
	}

	public boolean isManagerchatEnabled() {
		return managerchatEnabled;
	}

	public void setManagerchatEnabled(boolean managerchatEnabled) {
		this.managerchatEnabled = managerchatEnabled;
	}

	public MessageSettings getAdminchatSetting() {
		return adminchatSetting;
	}

	public void setAdminchatSetting(MessageSettings adminchatSetting) {
		this.adminchatSetting = adminchatSetting;
	}

	public boolean isAdminchatEnabled() {
		return adminchatEnabled;
	}

	public void setAdminchatEnabled(boolean adminchatEnabled) {
		this.adminchatEnabled = adminchatEnabled;
	}

	public boolean isStaffchatEnabled() {
		return staffchatEnabled;
	}

	public void setStaffchatEnabled(boolean staffchatEnabled) {
		this.staffchatEnabled = staffchatEnabled;
	}

	public MessageSettings getStaffchatSetting() {
		return staffchatSetting;
	}

	public void setStaffchatSetting(MessageSettings staffchatSetting) {
		this.staffchatSetting = staffchatSetting;
	}

}
