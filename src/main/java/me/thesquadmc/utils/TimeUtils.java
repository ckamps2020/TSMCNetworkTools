package me.thesquadmc.utils;

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

	public static long parseTime(String time) {
		long totalTime = 0L;
		boolean found = false;
		Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

		while (matcher.find()) {
			String s = matcher.group();
			Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
			String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
			switch (type) {
				case "s":
					totalTime += value;
					found = true;
					break;
				case "m":
					totalTime += value * 60;
					found = true;
					break;
				case "h":
					totalTime += value * 60 * 60;
					found = true;
					break;
				case "d":
					totalTime += value * 60 * 60 * 24;
					found = true;
					break;
				case "w":
					totalTime += value * 60 * 60 * 24 * 7;
					found = true;
					break;
				case "M":
					totalTime += value * 60 * 60 * 24 * 30;
					found = true;
					break;
				case "y":
					totalTime += value * 60 * 60 * 24 * 365;
					found = true;
					break;
			}
		}

		return !found ? -1 : totalTime * 1000;
	}

}
