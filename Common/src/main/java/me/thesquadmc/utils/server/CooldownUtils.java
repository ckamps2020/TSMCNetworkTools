package me.thesquadmc.utils.server;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class CooldownUtils {

    private static List<CooldownUtils> cooldowns = new ArrayList<>();
    public static Map<UUID, CooldownUtils> perPlayerCooldown = new HashMap<>();
    private long time;
    private long oldTime;
    private LongSupplier currentTime = () -> System.currentTimeMillis()/1000L;

    public CooldownUtils(int time, TimeUnit timeUnit, Runnable execute) {
        this.time = timeUnit.toSeconds(time);
        this.oldTime = System.currentTimeMillis()/1000L;

        cooldowns.add(this);

        execute.run();
    }

    public LongPredicate busy() {
        return t -> currentTime.getAsLong() < oldTime + t;
    }

    public void whenFinished(Runnable execute) {
        if (finished())
            execute.run();
    }

    private boolean finished() {
        cooldowns.remove(this);
        return currentTime.getAsLong() > oldTime + time - 1;
    }

    public long getCooldownTime() {
        return time;
    }

    public long getStartTime() {
        return  oldTime;
    }

    public static List<CooldownUtils> getRunningCooldowns() {
        return cooldowns;
    }

    public Supplier<Long> getCurrentTime() {
        return () -> currentTime.getAsLong() - oldTime + time;
    }

    public void cancel() {
        cooldowns.remove(this);
    }

    public static void cancelAllCooldowns() {
        cooldowns.forEach(c -> cooldowns.remove(c));
    }


}
