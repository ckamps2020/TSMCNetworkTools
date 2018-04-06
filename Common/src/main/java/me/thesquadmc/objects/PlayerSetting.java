package me.thesquadmc.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.thesquadmc.utils.enums.MessageSettings;

public class PlayerSetting<T> {

	private static final List<PlayerSetting<?>> VALUES = new ArrayList<>();
	
	// Player settings
	public static final PlayerSetting<Boolean> NOTIFICATIONS = new PlayerSetting<>("NOTIFICATIONS", Boolean.class, true);
	public static final PlayerSetting<Boolean> PRIVATE_MESSAGES = new PlayerSetting<>("PRIVATE_MESSAGES", "PMS", Boolean.class, true);
	public static final PlayerSetting<Boolean> FRIEND_CHAT = new PlayerSetting<>("FRIEND_CHAT", "FRIENDCHAT", Boolean.class, true);
	public static final PlayerSetting<Boolean> FRIEND_REQUESTS = new PlayerSetting<>("FRIEND_REQUESTS", "REQUESTS", Boolean.class, true);
	public static final PlayerSetting<Boolean> SOCIALSPY = new PlayerSetting<>("SOCIALSPY", Boolean.class, true);
	
	// Staff settings
	public static final PlayerSetting<MessageSettings> STAFFCHAT_SCOPE = new PlayerSetting<>("STAFFCHAT_SCOPE", MessageSettings.class, MessageSettings.GLOBAL);
	public static final PlayerSetting<MessageSettings> MANAGERCHAT_SCOPE = new PlayerSetting<>("MANAGERCHAT_SCOPE", MessageSettings.class, MessageSettings.GLOBAL);
	public static final PlayerSetting<MessageSettings> ADMINCHAT_SCOPE = new PlayerSetting<>("ADMINCHAT_SCOPE", MessageSettings.class, MessageSettings.GLOBAL);
	public static final PlayerSetting<Boolean> STAFFCHAT_ENABLED = new PlayerSetting<>("STAFFCHAT_ENABLED", Boolean.class, true);
	public static final PlayerSetting<Boolean> MANAGERCHAT_ENABLED = new PlayerSetting<>("MANAGERCHAT_ENABLED", Boolean.class, true);
	public static final PlayerSetting<Boolean> ADMINCHAT_ENABLED = new PlayerSetting<>("ADMINCHAT_ENABLED", Boolean.class, true);
	
	
	private final String name, legacyName;
	private final Class<T> settingType;
	private final T defaultValue;
	
	private PlayerSetting(String name, String legacyName, Class<T> settingType, T defaultValue) {
		this.name = name;
		this.legacyName = legacyName;
		this.settingType = settingType;
		this.defaultValue = defaultValue;
		
		VALUES.add(this);
	}
	
	private PlayerSetting(String name, Class<T> settingType, T defaultValue) {
		this(name, name, settingType, defaultValue);
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @deprecated This method should only be used for legacy data handling (i.e. database reading / writing) with
	 * regards to the deprecated Settings API. When switching databases from MySQL to MongoDB, this method SHOULD
	 * NOT BE USED. Instead, see {@link #getName()} and delete this method from the {@link PlayerSetting} class
	 */
	@Deprecated
	public String getLegacyName() {
		return legacyName;
	}
	
	public Class<T> getSettingType() {
		return settingType;
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public int hashCode() {
		return 31 + (name == null ? 0 : name.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerSetting)) return false;
		
		PlayerSetting<?> other = (PlayerSetting<?>) obj;
		return Objects.equals(name, other.name);
	}
	
	public static PlayerSetting<?>[] values() {
		return VALUES.toArray(new PlayerSetting<?>[VALUES.size()]);
	}
	
}