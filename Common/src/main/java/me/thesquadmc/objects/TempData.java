package me.thesquadmc.objects;

import me.thesquadmc.utils.msgs.StringUtils;
import org.bukkit.Bukkit;

/**
 * @deprecated Legacy API. See {@link TSMCUser}
 */
@Deprecated
public final class TempData {

	private String loginTime;
	private boolean staffchatEnabled;
	private boolean adminchatEnabled;
	private boolean managerchatEnabled;
	private boolean vanished;
	private boolean xray;
	private boolean forcefieldEnabled;
	private boolean ytVanishEnabled;
	private boolean monitor;
	private String realname;
	private boolean nickname;
	private String skinkey;
	private String signature;
	private boolean reportsEnabled;

	public TempData() {
		loginTime = StringUtils.getDate();
		staffchatEnabled = true;
		adminchatEnabled = true;
		managerchatEnabled = true;
		vanished = false;
		forcefieldEnabled = false;
		ytVanishEnabled = false;
		monitor = true;
		nickname = false;
		reportsEnabled = true;
		if (Bukkit.getServerName().toUpperCase().contains("FACTIONS")) {
			xray = true;
		} else {
			xray = false;
		}
	}

	public boolean isReportsEnabled() {
		return reportsEnabled;
	}

	public void setReportsEnabled(boolean reportsEnabled) {
		this.reportsEnabled = reportsEnabled;
	}


	public String getSkinkey() {
		return skinkey;
	}

	public void setSkinkey(String skinkey) {
		this.skinkey = skinkey;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public boolean isNicknamed() {
		return nickname;
	}

	public void setNickname(boolean nickname) {
		this.nickname = nickname;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
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

	public boolean isManagerchatEnabled() {
		return managerchatEnabled;
	}

	public void setManagerchatEnabled(boolean managerchatEnabled) {
		this.managerchatEnabled = managerchatEnabled;
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

}
