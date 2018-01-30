package me.thesquadmc.utils;

public final class TimeUtils {

	public static boolean elapsed(long from, long required) {
		return System.currentTimeMillis() - from > required;
	}

}
