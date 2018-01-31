package me.thesquadmc.utils;

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

}
