package me.thesquadmc.utils.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class CalendarUtils {

	public static String futureDateInDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime().toString();
	}

	public static String futureDateInHours(int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hours);
		return calendar.getTime().toString();
	}

	public static String futureDateInMinutes(int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime().toString();
	}

	public static Date futureDateInDaysDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public static Date futureDateInHoursDate(int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hours);
		return calendar.getTime();
	}

	public static Date futureDateInMinutesDate(int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public static Calendar convertStringToDate(String date) {
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			cal.setTime(sdf.parse(date));
			return cal;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isInPast(Date oldDate) {
		return oldDate.before(new Date());
	}

}
