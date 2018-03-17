package me.thesquadmc.utils.math;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtils {

	public static int getTotalPages(int amount) {
		return (int) Math.ceil((double)amount / 52);
	}

	public static boolean canContinue(int amount) {
		if (amount == 52 || amount == 52 * 2 || amount == 52 * 3 || amount == 52 * 4
				|| amount == 52 * 5 || amount == 52 * 6 || amount == 52 * 7) {
			return false;
		}
		return true;
	}

	public static double clamp(double value, double min, double max) {
		return (value < min ? min : (value > max ? max : value));
	}

	public static double percentage(double part, double total) {
		return percentage(part, total, 1);
	}

	public static double percentage(double part, double total, int degree) {
		return trim(part / total * 100.0D, degree);
	}

	public static int round(double a) {
		return (int) round(a, 1.0D);
	}

	public static double round(double a, double to) {
		boolean wasNegative = a < 0.0D;
		a = Math.abs(a);
		to = Math.abs(to);
		double quotient = a % to;
		return (wasNegative ? -1 : 1) * (quotient >= to / 2.0D ? a + (to - quotient) : a - quotient);
	}

	public static double trim(double d) {
		return trim(d, 1);
	}

	public static double trim(double d, int degree) {
		if ((Double.isNaN(d)) || (Double.isInfinite(d))) {
			d = 0.0D;
		}
		String format = "#.#";
		for (int i = 1; i < degree; i++) {
			format = format + "#";
		}
		try {
			return Double.valueOf(new DecimalFormat(format).format(d)).doubleValue();
		} catch (NumberFormatException exception) {
		}
		return d;
	}

	public static double square(double a) {
		return a * a;
	}

	public static double cube(double a) {
		return a * a * a;
	}

	public static boolean isInteger(String a) {
		try {
			Integer.parseInt(a);
			return true;
		} catch (NumberFormatException ignored) {
		}
		return false;
	}

	public static boolean isDouble(String a) {
		try {
			Double.parseDouble(a);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static int random(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	public static int random(int lower, int upper) {
		return lower >= upper ? upper : ThreadLocalRandom.current().nextInt(lower, upper);
	}

	public static double random(double lower, double upper) {
		return ThreadLocalRandom.current().nextDouble(lower, upper);
	}

}
