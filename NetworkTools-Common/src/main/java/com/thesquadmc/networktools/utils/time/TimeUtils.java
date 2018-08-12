package com.thesquadmc.networktools.utils.time;

import org.apache.commons.lang.math.NumberUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

    private static final Pattern TIME_PARSE_PATTERN = Pattern.compile("([0-9]+)([smhdwMy])");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssX", Locale.ENGLISH);
    private static final DecimalFormat FORMATTER = new DecimalFormat("00");

    public static Date parseDate(String string) {
        try {
            return DATE_FORMAT.parse(string.replace("T", ""));
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static long getTimeFromString(String timeString) {
        long time = 0L;
        if (timeString == null || timeString.isEmpty()) return time;

        Matcher matcher = TIME_PARSE_PATTERN.matcher(timeString);
        while (matcher.find()) {
            int count = NumberUtils.toInt(matcher.group(1), 0);
            char unit = matcher.group(2).charAt(0); // Only 1 char anyways

            switch (unit) {
                case 's': // Seconds
                    time += TimeUnit.SECONDS.toMillis(count);
                    break;
                case 'm': // Minutes
                    time += TimeUnit.MINUTES.toMillis(count);
                    break;
                case 'h': // Hours
                    time += TimeUnit.HOURS.toMillis(count);
                    break;
                case 'd': // Days
                    time += TimeUnit.DAYS.toMillis(count);
                    break;
                case 'w': // Weeks
                    time += TimeUnit.DAYS.toMillis(7 * count);
                    break;
                case 'M': // Months
                    for (int m = 0; m < count; m++) {
                        time += TimeUnit.DAYS.toMillis(YearMonth.now().plusMonths(m).lengthOfMonth());
                    }
                    break;
                case 'y': // Years
                    for (int y = 0; y < count; y++) {
                        time += TimeUnit.DAYS.toMillis(Year.now().plusYears(y).length());
                    }
                    break;
            }
        }

        return time;
    }

    public static long now() {
        return nowMillis() / 1000L;
    }

    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    public static String formatTs(String ts) {
        long timestamp = Long.parseLong(ts) * 1000L;
        return new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(timestamp);
    }

    public static boolean isSameDay(long ts1, long ts2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(ts1));
        cal2.setTime(new Date(ts2));

        return (cal1.get(1) == cal2.get(1)) &&
                (cal1.get(6) == cal2.get(6));
    }

    public static String getFormattedTime(int seconds) {
        return getFormattedTime((long) seconds * 1000);
    }

    public static String getFormattedTime(long millis) {
        boolean ago = false;
        if (millis < 0) {
            ago = true;

            millis = Math.abs(millis);
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder();
        if (days != 0) {
            sb.append(days).append("d");
        }

        if (hours != 0) {
            sb.append(" ").append(hours).append("h");
        }

        if (minutes != 0) {
            sb.append(" ").append(minutes).append("m");
        }

        if (seconds != 0) {
            sb.append(" ").append(seconds).append("s");
        }

        return (sb.toString().trim() + (ago ? " ago" : ""));
    }
}
