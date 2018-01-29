package me.thesquadmc.utils;

import net.md_5.bungee.api.ChatColor;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtils {

	public static String msg(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String formatTime(long time) {
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

}
