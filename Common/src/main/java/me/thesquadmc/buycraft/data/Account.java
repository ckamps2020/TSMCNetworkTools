package me.thesquadmc.buycraft.data;

import me.thesquadmc.utils.json.JSONUtils;
import org.json.JSONObject;

public final class Account {
	private int id;
	private String domain;
	private String name;

	private String currencyName;
	private String currencySymbol;

	private boolean onlineMode;

	public Account(int id, String domain, String name, String currencyName, String currencySymbol, boolean onlineMode) {
		this.id = id;
		this.domain = domain;
		this.name = name;
		this.currencyName = currencyName;
		this.currencySymbol = currencySymbol;
		this.onlineMode = onlineMode;
	}

	public Account(JSONObject object, JSONObject currency){
		this(JSONUtils.safeGetInt(object, "id"), JSONUtils.safeGetString(object, "domain"), JSONUtils.safeGetString(object, "name"), JSONUtils.safeGetString(currency, "iso_4217"), JSONUtils.safeGetString(currency, "symbol"), JSONUtils.safeGetBoolean(object, "online_mode"));
	}

	public int getId() {
		return id;
	}

	public String getDomain() {
		return domain;
	}

	public String getName() {
		return name;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public boolean isOnlineMode() {
		return onlineMode;
	}

}
