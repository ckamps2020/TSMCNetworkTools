package me.thesquadmc.utils;

import me.thesquadmc.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

	public static String msg(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
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

	public static BaseComponent[] getHoverMessage(String message, String hoverMessage) {
		BaseComponent[] components = TextComponent.fromLegacyText(msg(message));
		BaseComponent[] hoverText = TextComponent.fromLegacyText(msg(hoverMessage));
		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
		for (BaseComponent component : components) {
			component.setHoverEvent(hoverEvent);
		}
		return components;
	}

	private static final Pattern URL_REGEX = Pattern.compile(
			"^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
	private static final Pattern IP_REGEX = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
	private static final List<String> LINK_WHITELIST = Arrays.asList(
			// Our stuff
			"thesquadmc.net",

			// Social media
			"youtube.com", "youtu.be", "discord.gg", "twitter.com",

			// Images
			"prnt.sc", "gyazo.com", "imgur.com"
	);

	public static boolean shouldFilter(String message) {
		String msg = message.toLowerCase()
				.replace("3", "e")
				.replace("1", "i")
				.replace("!", "i")
				.replace("@", "a")
				.replace("7", "t")
				.replace("0", "o")
				.replace("5", "s")
				.replace("8", "b")
				.replaceAll("\\p{Punct}|\\d", "").trim();
		String[] words = msg.trim().split(" ");
		for (String word : words) {
			if (!word.matches("[a-zA-Z0-9]*")) {
				return true;
			}
			for (String filteredWord : Main.getMain().getFilteredWords()) {
				if (word.contains(filteredWord)) {
					return true;
				}
			}
		}

		for (String word : message.replace("(dot)", ".").replace("[dot]", ".").trim().split(" ")) {
			boolean continueIt = false;
			for (String phrase : LINK_WHITELIST) {
				if (word.toLowerCase().contains(phrase)) {
					continueIt = true;
					break;
				}
			}

			if (continueIt) {
				continue;
			}

			Matcher matcher = IP_REGEX.matcher(word);
			if (matcher.matches()) {
				return true;
			}

			matcher = URL_REGEX.matcher(word);
			if (matcher.matches()) {
				return true;
			}
		}

		Optional<String> optional = Main.getMain().getFilteredPhrases().stream().filter(msg::contains)
				.findFirst();
		return optional.isPresent();
	}

}
