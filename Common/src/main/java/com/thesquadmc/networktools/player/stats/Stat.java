package com.thesquadmc.networktools.player.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stat<T> {

    public static final List<Stat<?>> VALUES = new ArrayList<>();

    public static Stat<Long> PLAYTIME = new Stat<>("play_time", Long.class, 0L);
    public static Stat<Integer> LOGINS = new Stat<>("logins", Integer.class, 0);
    public static Stat<Integer> BLOCKS_BROKEN = new Stat<>("blocks_broken", Integer.class, 0);

    private final String name;
    private final Class<T> statsType;
    private final T defaultValue;

    private Stat(String name, Class<T> statsType, T defaultValue) {
        this.name = name;
        this.statsType = statsType;
        this.defaultValue = defaultValue;

        VALUES.add(this);
    }

    public static Stat<?> valueOf(String name) {
        for (Stat<?> value : VALUES) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public Class<T> getStatsType() {
        return statsType;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Stat)) return false;

        Stat<?> other = (Stat<?>) obj;
        return Objects.equals(name, other.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
