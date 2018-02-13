package me.thesquadmc.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

	public static boolean elapsed(long from, long required) {
		return System.currentTimeMillis() - from > required;
	}

	public static String millisToRoundedTime(long millis) {
		millis += 1L;

		long seconds = millis / 1000L;
		long minutes = seconds / 60L;
		long hours = minutes / 60L;
		long days = hours / 24L;
		long weeks = days / 7L;
		long months = weeks / 4L;
		long years = months / 12L;

		if (years > 0) {
			return years + " year" + (years == 1 ? "" : "s");
		} else if (months > 0) {
			return months + " month" + (months == 1 ? "" : "s");
		} else if (weeks > 0) {
			return weeks + " week" + (weeks == 1 ? "" : "s");
		} else if (days > 0) {
			return days + " day" + (days == 1 ? "" : "s");
		} else if (hours > 0) {
			return hours + " hour" + (hours == 1 ? "" : "s");
		} else if (minutes > 0) {
			return minutes + " minute" + (minutes == 1 ? "" : "s");
		} else {
			return seconds + " second" + (seconds == 1 ? "" : "s");
		}
	}

	public static String convertTime(int totalSecs) {
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		return String.valueOf(minutes) + "m" + seconds + "s";
	}

	public static String convertPlaytime(int minutes) {
		int hours = minutes / 60;
		int min = minutes % 60;
		return hours + "h" + min + "m";
	}

	public static String convert(int totalSecs) {
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		if (seconds < 10) {
			return String.valueOf(minutes) + ":0" + seconds;
		}
		return String.valueOf(minutes) + ":" + seconds;
	}

	public static String convert(long totalMillieSecs) {
		int totalSecs = Long.valueOf(totalMillieSecs/1000).intValue();
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		if (seconds < 10) {
			return String.valueOf(minutes) + ":0" + seconds;
		}
		return String.valueOf(minutes) + ":" + seconds;
	}

	private static final DecimalFormat FORMATTER = new DecimalFormat("00");

	public static long now() {
		return nowMillis() / 1000L;
	}

	public static long nowMillis() {
		return System.currentTimeMillis();
	}

	public static String formatTime(long time) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		double secs = time / 1000L;
		double mins = secs / 60.0D;
		double hours = mins / 60.0D;
		double days = hours / 24.0D;
		if (mins < 1.0D) {
			return decimalFormat.format(secs) + " Seconds";
		}
		if (hours < 1.0D) {
			return decimalFormat.format(mins % 60.0D) + " Minutes";
		}
		if (days < 1.0D) {
			return decimalFormat.format(hours % 24.0D) + " Hours";
		}
		return decimalFormat.format(days) + " Days";
	}

	public static String formatTs(String ts) {
		long timestamp = Long.parseLong(ts) * 1000L;
		return new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Long.valueOf(timestamp));
	}

	public static boolean isSameDay(long ts1, long ts2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(new Date(ts1));
		cal2.setTime(new Date(ts2));

		return (cal1.get(1) == cal2.get(1)) &&
				(cal1.get(6) == cal2.get(6));
	}

}
