package com.thesquadmc.networktools.player;

import com.thesquadmc.networktools.NetworkTools;

import java.util.Objects;

public class PlayerSetting<T> {

    // Player settings
    public static final PlayerSetting<Boolean> PRIVATE_MESSAGES = new PlayerSetting<>("PRIVATE_MESSAGES", Boolean.class, true);
    public static final PlayerSetting<Boolean> FRIEND_NOTIFICATIONS = new PlayerSetting<>("FRIEND_NOTIFICATIONS", Boolean.class, true);
    public static final PlayerSetting<Boolean> FRIEND_CHAT = new PlayerSetting<>("FRIEND_CHAT", Boolean.class, true);
    public static final PlayerSetting<Boolean> FRIEND_REQUESTS = new PlayerSetting<>("FRIEND_REQUESTS", Boolean.class, true);
    public static final PlayerSetting<Boolean> SOCIALSPY = new PlayerSetting<>("SOCIALSPY", Boolean.class, true);
    public static final PlayerSetting<Boolean> TELEPORT_REQUESTS = new PlayerSetting<>("TELEPORT_REQUESTS", Boolean.class, true);

    // Staff settings
    public static final PlayerSetting<Boolean> STAFFCHAT_ENABLED = new PlayerSetting<>("STAFFCHAT_ENABLED", Boolean.class, true);
    public static final PlayerSetting<Boolean> MANAGERCHAT_ENABLED = new PlayerSetting<>("MANAGERCHAT_ENABLED", Boolean.class, true);
    public static final PlayerSetting<Boolean> ADMINCHAT_ENABLED = new PlayerSetting<>("ADMINCHAT_ENABLED", Boolean.class, true);
    public static final PlayerSetting<Boolean> REPORTS = new PlayerSetting<>("REPORTS", Boolean.class, true);
    public static final PlayerSetting<Boolean> MONITOR = new PlayerSetting<>("MONITOR", Boolean.class, true);
    public static final PlayerSetting<Boolean> FORCEFIELD = new PlayerSetting<>("FORCEFIELD", Boolean.class, false);
    public static final PlayerSetting<Boolean> XRAY_NOTIFICATION = new PlayerSetting<>("XRAY_NOTIFICATION", Boolean.class, true);
    public static final PlayerSetting<Boolean> VANISHED = new PlayerSetting<>("VANISHED", Boolean.class, false);
    public static final PlayerSetting<Boolean> YOUTUBE_VANISHED = new PlayerSetting<>("YOUTUBE_VANISHED", Boolean.class, false);

    public static final PlayerSetting<String> NICKNAME = new PlayerSetting<>("NICKNAME", String.class, "");

    private final String name;
    private final Class<T> settingType;
    private final T defaultValue;

    private PlayerSetting(String name, Class<T> settingType, T defaultValue) {
        this.name = name;
        this.settingType = settingType;
        this.defaultValue = defaultValue;

        NetworkTools.getInstance().registerPlayerSetting(this);
    }

    public String getName() {
        return name;
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

}