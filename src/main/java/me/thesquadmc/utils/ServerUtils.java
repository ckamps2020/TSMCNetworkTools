package me.thesquadmc.utils;

import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

public final class ServerUtils {

	private static final String name = Bukkit.getServer().getClass().getPackage().getName();
	private static final String version = name.substring(name.lastIndexOf('.') + 1);
	private static final DecimalFormat format = new DecimalFormat("##.##");
	private static Object serverInstance;
	private static Field tpsField;

	public static String getTPS(int time) {
		try {
			serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
			tpsField = serverInstance.getClass().getField("recentTps");
			double[] tps = ((double[]) tpsField.get(serverInstance));
			return format.format(tps[time]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
