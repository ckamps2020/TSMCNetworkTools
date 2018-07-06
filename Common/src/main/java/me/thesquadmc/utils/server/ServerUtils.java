package me.thesquadmc.utils.server;

import com.sun.management.OperatingSystemMXBean;
import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Locale;

public final class ServerUtils {

    private static final String name = Bukkit.getServer().getClass().getPackage().getName();
    private static final String version = name.substring(name.lastIndexOf('.') + 1);
    private static final DecimalFormat format = new DecimalFormat("##.##");
    private static final OperatingSystemMXBean OS = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void calculateServerType() {
        String m = Bukkit.getServerName().toUpperCase();
        if (m.startsWith(ServerType.BEDWARS_DUOS)) {
            Main.getMain().setServerType(ServerType.BEDWARS_DUOS);
        } else if (m.startsWith(ServerType.BEDWARS_FOURS)) {
            Main.getMain().setServerType(ServerType.BEDWARS_FOURS);
        } else if (m.startsWith(ServerType.BEDWARS_SOLO)) {
            Main.getMain().setServerType(ServerType.BEDWARS_SOLO);
        } else if (m.startsWith(ServerType.BEDWARS_THREES)) {
            Main.getMain().setServerType(ServerType.BEDWARS_THREES);
        } else if (m.startsWith(ServerType.MINIGAME_HUB)) {
            Main.getMain().setServerType(ServerType.MINIGAME_HUB);
        } else if (m.startsWith(ServerType.FACTIONS)) {
            Main.getMain().setServerType(ServerType.FACTIONS);
        } else if (m.startsWith(ServerType.SKYBLOCK)) {
            Main.getMain().setServerType(ServerType.SKYBLOCK);
        } else if (m.startsWith(ServerType.PRISON)) {
            Main.getMain().setServerType(ServerType.PRISON);
        } else if (m.startsWith(ServerType.TROLLWARS)) {
            Main.getMain().setServerType(ServerType.TROLLWARS);
        } else if (m.startsWith(ServerType.HUB)) {
            Main.getMain().setServerType(ServerType.HUB);
        }
    }

    public static void safeShutdown() {
        System.out.println("[NetworkTools] Server restarting in 3 seconds...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(CC.translate("&e&lSTOP &6■ &7Server is restarting!"));
        }
        Bukkit.getScheduler().runTaskLater(Main.getMain(), Bukkit::shutdown, 3 * 20);
    }

    public static String getTPS(int time) {
        try {
            Object serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
            Field tpsField = serverInstance.getClass().getField("recentTps");
            double[] tps = ((double[]) tpsField.get(serverInstance));
            return format.format(tps[time]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePropertiesFile() {
        Main.getMain().getNMSAbstract().savePropertiesFile();
    }

    public static <T> void setServerProperty(ServerProperty<T> property, T value) {
        Main.getMain().getNMSAbstract().setServerProperty(property, value);
    }

    public static void updateServerState(String serverState) {
        Main.getMain().setServerState(serverState);

        Main.getMain().getRedisManager().sendMessage(RedisChannels.SERVER_STATE, RedisMesage.newMessage()
                .set(RedisArg.SERVER, Bukkit.getServerName())
                .set(RedisArg.SERVER_STATE, serverState));
    }

    public static String getSystemCpuLoadFormatted() {
        return new DecimalFormat("0.0").format(OS.getSystemCpuLoad() * 100) + "%";
    }

    public static String getProcessCpuLoadFormatted() {
        return new DecimalFormat("0.0").format(OS.getProcessCpuLoad() * 100) + "%";
    }

    public static String getFreeMemory() {
        return humanReadableByteCount(Runtime.getRuntime().freeMemory());
    }

    public static String getUsedMemory() {
        return humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    public static String getTotalMemory() {
        return humanReadableByteCount(Runtime.getRuntime().totalMemory());
    }

    public static String getMemoryPercentageUsed() {
        return DecimalFormat.getPercentInstance(Locale.US).format(
                ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) * 1.0) / Runtime.getRuntime().totalMemory()
        );
    }

    private static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));

        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
