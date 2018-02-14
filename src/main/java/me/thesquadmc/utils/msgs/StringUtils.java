package me.thesquadmc.utils.msgs;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

	public static Map<UUID, String> lastMsg = new HashMap<>();

	public StringUtils() {
		populate();
	}

	public static Map<UUID, String> getLastMsg() {
		return lastMsg;
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
		BaseComponent[] components = TextComponent.fromLegacyText(CC.translate(message));
		BaseComponent[] hoverText = TextComponent.fromLegacyText(CC.translate(hoverMessage));
		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
		for (BaseComponent component : components) {
			component.setHoverEvent(hoverEvent);
		}
		return components;
	}

	public static BaseComponent[] getHoverMessage(String message, String hoverMessage, String command) {
		BaseComponent[] components = TextComponent.fromLegacyText(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
		BaseComponent[] hoverText = TextComponent.fromLegacyText(org.bukkit.ChatColor.translateAlternateColorCodes('&', hoverMessage));
		ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
		for (BaseComponent component : components) {
			component.setClickEvent(clickEvent);
			component.setHoverEvent(hoverEvent);
		}
		return components;
	}

	private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";
	private static final String SPLIT_PATTERN = Pattern.compile("\\s").pattern();

	private static final String VOWELS = "aeiou";

	public static List<String> splitString(String message) {
		List<String> strings = new ArrayList<>();
		for (String string : message.split("(?<=\\G.{30})")) {
			strings.add(string);
		}
		return strings;
	}

	public static String toNiceString(String string) {
		string = org.bukkit.ChatColor.stripColor(string).replace('_', ' ').toLowerCase();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.toCharArray().length; i++) {
			char c = string.toCharArray()[i];
			if (i > 0) {
				char prev = string.toCharArray()[i - 1];
				if (prev == ' ' || prev == '[' || prev == '(') {
					if (i == string.toCharArray().length - 1 || c != 'x' ||
							!Character.isDigit(string.toCharArray()[i + 1])) {
						c = Character.toUpperCase(c);
					}
				}
			} else {
				if (c != 'x' || !Character.isDigit(string.toCharArray()[i + 1])) {
					c = Character.toUpperCase(c);
				}
			}
			sb.append(c);
		}

		return sb.toString();
	}

	public static String buildMessage(String[] args, int start) {
		if (start >= args.length) {
			return "";
		}
		return org.bukkit.ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
	}

	public static String getFirstSplit(String s) {
		return s.split(SPLIT_PATTERN)[0];
	}

	public static String getAOrAn(String input) {
		return ((VOWELS.contains(input.substring(0, 1).toLowerCase())) ? "an" : "a");
	}

	public static String fixStringForCaps(String message) {
		int upperCase = 0;
		int lowerCase = 0;
		for (int k = 0; k < message.length(); k++) {
			if (Character.isUpperCase(message.charAt(k))) upperCase++;
			if (Character.isLowerCase(message.charAt(k))) lowerCase++;
		}

		if (upperCase > lowerCase) {
			return message.toLowerCase();
		}
		return message;
	}

	private static final Set<String> curses = new HashSet<>();
	private static final Pattern URL_REGEX = Pattern.compile(
			"^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
	private static final Pattern IP_REGEX = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
	private static final List<String> LINK_WHITELIST = Arrays.asList(
			// Our stuff
			"thesquadmc.net"
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
				.trim();

		for (String word : message.trim().split(" ")) {
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

		return isFiltered(msg);
	}

	private static boolean isFiltered(String message) {
		return containsCurses(message);
	}

	private static boolean containsCurses(String message) {
		for (String curse : curses) {
			if (message.toLowerCase().contains(curse.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private static void populate() {
		if (!curses.isEmpty()) {
			return;
		}
		curses.add("cunt");
		curses.add("nazi");
		curses.add("hitler");
		curses.add("h1tler");
		curses.add("h1tl3r");
		curses.add("hitl3r");
		curses.add("fag");
		curses.add("f4g");
		curses.add("gay");
		curses.add("g4y");
		curses.add("homo");
		curses.add(" rape ");
		curses.add("r4pe");
		curses.add("r4p3");
		curses.add("faggot");
		curses.add(" feg ");
		curses.add("f4ggot");
		curses.add("f4gg0t");
		curses.add("whore");
		curses.add("fegget");

		curses.add("gay");
		curses.add("g4y");
		curses.add("4r5e");
		curses.add("5h1t");
		curses.add("5hit");
		curses.add("a55");
		curses.add(" anal ");
		curses.add("anus");
		curses.add("ar5e");
		curses.add("arrse");
		curses.add(" arse ");
		curses.add(" ass ");
		curses.add("ass-fucker");
		curses.add(" asses ");
		curses.add("assfucker");
		curses.add("assfukka");
		curses.add("asshole");
		curses.add("assholes");
		curses.add("asswhole");
		curses.add("a_s_s");
		curses.add("b!tch");
		curses.add("b00bs");
		curses.add("b17ch");
		curses.add("b1tch");
		curses.add("ballbag");

		curses.add("ballsack");
		curses.add("bastard");
		curses.add("beastial");
		curses.add("beastiality");
		curses.add("bellend");
		curses.add("bestial");
		curses.add("bestiality");
		curses.add("bi+ch");
		curses.add("biatch");
		curses.add("bitch");
		curses.add("b1tch");
		curses.add("bitcher");
		curses.add("bitchers");
		curses.add("bitches");
		curses.add("bitchin");
		curses.add("bitching");
		curses.add("bleach");
		curses.add("bleech");
		curses.add("blow job");
		curses.add("blowjob");
		curses.add("blowjobs");
		curses.add("boiolas");
		curses.add("boner");
		curses.add("boob");
		curses.add("boobs");
		curses.add("booobs");
		curses.add("boooobs");
		curses.add("booooobs");
		curses.add("boooooobs");
		curses.add("booooooobs");
		curses.add("breasts");
		curses.add("buceta");
		curses.add("bugger");

		curses.add("bunny fucker");
		curses.add("butthole");
		curses.add("buttmuch");
		curses.add("buttplug");
		curses.add("bullshit");
		curses.add(" bs ");
		curses.add("c0ck");
		curses.add("c0cksucker");
		curses.add("carpet muncher");
		curses.add("cawk");
		curses.add("chink");
		curses.add(" cipa ");
		curses.add("cl1t");
		curses.add("clit");
		curses.add("clitoris");
		curses.add("clits");
		curses.add("cnut");
		curses.add("cock");
		curses.add("cock-sucker");
		curses.add("cockface");
		curses.add("cockhead");
		curses.add("cockmunch");
		curses.add("cockmuncher");
		curses.add("cocks");
		curses.add("cocksuck");
		curses.add("cocksucked");
		curses.add("cocksucker");
		curses.add("cocksucking");
		curses.add("cocksucks");
		curses.add("cocksuka");
		curses.add("cocksukka");
		curses.add("cok");
		curses.add("cokmuncher");
		curses.add("coksucka");
		curses.add(" coon ");

		curses.add(" cum ");
		curses.add("cummer");
		curses.add("cumming");
		curses.add("cuming");
		curses.add("cums");
		curses.add("cumshot");
		curses.add("cunilingus");
		curses.add("cunillingus");
		curses.add("cunnilingus");
		curses.add("cunt");
		curses.add("cuntlick");
		curses.add("cuntlicker");
		curses.add("cuntlicking");
		curses.add("cunts");
		curses.add("cyalis");
		curses.add("cyberfuc");
		curses.add("cyberfuck");
		curses.add("cyberfucked");
		curses.add("cyberfucker");
		curses.add("cyberfuckers");
		curses.add("cyberfucking");
		curses.add("d1ck");

		curses.add("dick");
		curses.add("dickhead");
		curses.add("dildo");
		curses.add("dildos");
		curses.add("dink");
		curses.add("dinks");
		curses.add("dirsa");
		curses.add("dlck");
		curses.add("dog-fucker");
		curses.add("doggin");
		curses.add("dogging");
		curses.add("donkeyribber");
		curses.add("doosh");
		curses.add("duche");
		curses.add("dyke");
		curses.add("ebola");
		curses.add("ejaculate");
		curses.add("ejaculated");
		curses.add("ejaculates");
		curses.add("ejaculating");
		curses.add("ejaculatings");
		curses.add("ejaculation");
		curses.add("ejakulate");
		curses.add("f u c k");
		curses.add("f u c k e r");
		curses.add("f4nny");
		curses.add("fag");
		curses.add("fagging");
		curses.add("faggitt");
		curses.add("faggot");
		curses.add("faggs");
		curses.add("fagot");
		curses.add("fagots");
		curses.add("fags");
		curses.add("fanny");
		curses.add("fannyflaps");
		curses.add("fannyfucker");
		curses.add("fanyy");
		curses.add("fatass");
		curses.add("fcuk");
		curses.add("fcuker");
		curses.add("fcuking");
		curses.add("feck");
		curses.add("fecker");
		curses.add("felching");
		curses.add("fellate");
		curses.add("fellatio");
		curses.add("fingerfuck");
		curses.add("fingerfucked");
		curses.add("fingerfucker");
		curses.add("fingerfuckers");
		curses.add("fingerfucking");
		curses.add("fingerfucks");
		curses.add("fistfuck");
		curses.add("fistfucked");
		curses.add("fistfucker");
		curses.add("fistfuckers");
		curses.add("fistfucking");
		curses.add("fistfuckings");
		curses.add("fistfucks");
		curses.add("flange");
		curses.add("fook");
		curses.add("fooker");
		curses.add("fuck");
		curses.add("f u c k");
		curses.add(" ffs ");
		curses.add("fucknig");
		curses.add("fucknigs");
		curses.add("fuckoff");

		curses.add("fukc");
		curses.add("fucka");
		curses.add("fucked");
		curses.add("fucker");
		curses.add("fuckers");
		curses.add("fuckhead");
		curses.add("fuckheads");
		curses.add("fuckin");
		curses.add("fukcin");
		curses.add("fucking");
		curses.add(" fking ");
		curses.add("fkin");
		curses.add("fcknig");
		curses.add("fuckign");
		curses.add("fukcing");
		curses.add("fuckings");
		curses.add("fuckingshitmotherfucker");
		curses.add("fuckme");
		curses.add("fucks");
		curses.add("fuckwhit");
		curses.add("fuckwit");
		curses.add("fudge packer");
		curses.add("fudgepacker");
		curses.add("fuk");
		curses.add("fuker");
		curses.add("fukker");
		curses.add("fukkin");
		curses.add("fuks");
		curses.add("fukwhit");
		curses.add("fukwit");
		curses.add("fux");
		curses.add("fux0r");
		curses.add("f_u_c_k");
		curses.add("gangbang");
		curses.add("gangbanged");
		curses.add("gangbangs");
		curses.add("gaylord");
		curses.add("gaysex");
		curses.add("goatse");
		curses.add("god-dam");
		curses.add("god-damned");
		curses.add("goddamn");
		curses.add("goddamned");
		curses.add("hardcoresex");

		curses.add("heshe");
		curses.add("hoar");
		curses.add("hoare");
		curses.add("hoer");
		curses.add("homo");
		curses.add(" hore ");
		curses.add("horniest");
		curses.add("horny");
		curses.add("hotsex");
		curses.add("jack-off");
		curses.add("jackoff");
		curses.add(" jap ");
		curses.add("jerk-off");
		curses.add("jism");
		curses.add("jiz");
		curses.add("jizm");
		curses.add("jizz");
		curses.add("kawk");
		curses.add("knob");
		curses.add("knobead");
		curses.add("knobed");
		curses.add("knobend");
		curses.add("knobhead");
		curses.add("knobjocky");
		curses.add("knobjokey");
		curses.add("kock");
		curses.add("kondum");
		curses.add("kondums");
		curses.add("kum");
		curses.add("kummer");
		curses.add("kumming");
		curses.add("kums");
		curses.add("kunilingus");
		curses.add("l3i+ch");
		curses.add("l3itch");
		curses.add("labia");

		curses.add("lusting");
		curses.add("m0f0");
		curses.add("m0fo");
		curses.add("m45terbate");
		curses.add("ma5terb8");
		curses.add("ma5terbate");
		curses.add("masochist");
		curses.add("master-bate");
		curses.add("masterb8");
		curses.add("masterbat*");
		curses.add("masterbat3");
		curses.add("masterbate");
		curses.add("masterbation");
		curses.add("masterbations");
		curses.add("masturbate");
		curses.add("mo-fo");
		curses.add("mof0");
		curses.add("mofo");
		curses.add("mothafuck");
		curses.add("mothafucka");
		curses.add("mothafuckas");
		curses.add("mothafuckaz");
		curses.add("mothafucked");
		curses.add("mothafucker");
		curses.add("mothafuckers");
		curses.add("mothafuckin");
		curses.add("mothafucking");
		curses.add("mothafuckings");
		curses.add("mothafucks");
		curses.add("mother fucker");
		curses.add("motherfuck");
		curses.add("motherfucked");
		curses.add("motherfucker");
		curses.add("motherfuckers");
		curses.add("motherfuckin");
		curses.add("motherfucking");
		curses.add("motherfuckings");
		curses.add("motherfuckka");
		curses.add("motherfucks");
		curses.add(" muff ");
		curses.add("mutha");
		curses.add("muthafecker");
		curses.add("muthafuckker");
		curses.add("muther");
		curses.add("mutherfucker");
		curses.add("n1gga");
		curses.add("n1gger");
		curses.add("nazi");
		curses.add("nigg3r");
		curses.add("nigg4h");
		curses.add("nigga");
		curses.add("niggah");
		curses.add("niggas");
		curses.add("niggaz");
		curses.add("nigger");
		curses.add("niggre");
		curses.add("niggers");

		curses.add("nob jockey");
		curses.add("nobhead");
		curses.add("nobjocky");
		curses.add("nobjokey");
		curses.add("numbnuts");
		curses.add("nutsack");
		curses.add("orgasim");
		curses.add("orgasims");
		curses.add("orgasm");
		curses.add("orgasms");
		curses.add("p0rn");
		curses.add("pecker");
		curses.add("penis");
		curses.add("pen15");
		curses.add("penisfucker");
		curses.add("phonesex");
		curses.add("phuck");
		curses.add("phuk");
		curses.add("phuked");
		curses.add("phuking");
		curses.add("phukked");
		curses.add("phukking");
		curses.add("phuks");
		curses.add("phuq");
		curses.add("pigfucker");
		curses.add("pimpis");
		curses.add("piss");
		curses.add("pissed");
		curses.add("pisser");
		curses.add("pissers");
		curses.add("pisses");
		curses.add("pissflaps");
		curses.add("pissin");
		curses.add("pissing");
		curses.add("pissoff");
		curses.add("porn");
		curses.add("porno");
		curses.add("pornography");
		curses.add("pornos");
		curses.add("prick");
		curses.add("pricks");
		curses.add(" pron ");
		curses.add("prostitute");
		curses.add("pube");
		curses.add("pusse");
		curses.add("pussi");
		curses.add("pussies");
		curses.add("pussy");
		curses.add("pu$$y");
		curses.add("pussys");
		curses.add(" pusy ");
		curses.add("rectum");
		curses.add("retard");
		curses.add("rimjaw");
		curses.add("rimming");
		curses.add("s hit");
		curses.add("s.o.b.");
		curses.add("sadist");
		curses.add("schlong");
		curses.add("screwing");
		curses.add("scroat");
		curses.add("scrote");
		curses.add("scrotum");
		curses.add(" semen ");
		curses.add("sex");
		curses.add("sh!+");
		curses.add("sh!t");
		curses.add("sh1t");
		curses.add("shag");
		curses.add("shagger");
		curses.add("shaggin");
		curses.add("shagging");
		curses.add("shemale");
		curses.add("shi+");
		curses.add("shit");
		curses.add(" shat ");
		curses.add(" sh*t ");
		curses.add("siht");
		curses.add("shitdick");
		curses.add("shite");
		curses.add("shited");
		curses.add("shitey");
		curses.add("shitfuck");
		curses.add("shitfull");
		curses.add("shithead");
		curses.add("shiting");
		curses.add("shitings");
		curses.add("shits");
		curses.add("shitted");
		curses.add("shitter");
		curses.add("shitters");
		curses.add("shitting");
		curses.add("shittings");
		curses.add("shitty");
		curses.add("skank");
		curses.add("slut");
		curses.add("sluts");
		curses.add("smegma");
		curses.add("smut");
		curses.add("snatch");
		curses.add("son-of-a-bitch");

		curses.add("spunk");
		curses.add("s_h_i_t");
		curses.add("t1tt1e5");
		curses.add("t1tties");
		curses.add("teets");
		curses.add("teez");
		curses.add("testical");
		curses.add("testicle");

		curses.add("titfuck");
		curses.add("tits");
		curses.add("titt");
		curses.add("tittie5");
		curses.add("tittiefucker");
		curses.add("titties");
		curses.add("tittyfuck");
		curses.add("tittywank");
		curses.add("titwank");
		curses.add("tosser");
		curses.add(" turd ");
		curses.add("tw4t");
		curses.add("twat");
		curses.add("twathead");
		curses.add("twatty");
		curses.add("twunt");
		curses.add("twunter");
		curses.add("v14gra");
		curses.add("v1gra");
		curses.add("vagina");
		curses.add("viagra");
		curses.add("vulva");
		curses.add("w00se");
		curses.add("wang");
		curses.add("wank");
		curses.add("wanker");
		curses.add("wanky");
		curses.add("whoar");
		curses.add("whore");
		curses.add("puto");
		curses.add("vagin");

		curses.add("kanker");
		curses.add("fagt");
		curses.add("fgt");
		curses.add("kut");
		curses.add("dushbag");
		curses.add("duhbag");
		curses.add("queer");
		curses.add("bleach");
		curses.add("muthafucka");
		curses.add("muthafuckas");
		curses.add("badnez");

		curses.add(" puta ");
		curses.add("bosta");
		curses.add("caralho");
		curses.add("porra");
		curses.add("kct");
		curses.add("catece");
		curses.add("krl");
		curses.add("buceta");
		curses.add("viado");
		curses.add("merda");
		curses.add("xavaska");
		curses.add("chavasca");
		curses.add("chavaska");
		curses.add("xota");
		curses.add("xoxota");
		curses.add("xxt");
		curses.add("erotico");

		curses.add("ninfeta");
		curses.add("vibrador");

		curses.add("boquete");
		curses.add("gozo");
		curses.add("gozar");
		curses.add("suruba");
		curses.add("sapato");
		curses.add("jiromba");
		curses.add("giromba");
		curses.add("pepeca");
		curses.add("fetiche");
		curses.add("grelho");
		curses.add("chupada");
		curses.add("fuder");
		curses.add("foder");
		curses.add("rola");
		curses.add("piroca");
		curses.add("cabaa");
		curses.add(" bago ");
		curses.add("cagar");
		curses.add("teta");
		curses.add("tetas");
		curses.add("bronha");
		curses.add("punheta");
		curses.add("xereca");
		curses.add("cuzo");
		curses.add("mijo");
		curses.add("mija");
		curses.add("naba");
		curses.add("pentelho");
		curses.add("rola");
		curses.add("roludo");
		curses.add("tezao");
		curses.add("tesao");

		curses.add("2g1c");
		curses.add("2 girls 1 cup");
		curses.add("acrotomophilia");
		curses.add("alabama hot pocket");
		curses.add("alaskan pipeline");
		curses.add("anilingus");
		curses.add("apeshit");
		curses.add("arsehole");
		curses.add("assmunch");
		curses.add("auto erotic");
		curses.add("autoerotic");
		curses.add("babeland");
		curses.add("baby batter");
		curses.add("baby juice");
		curses.add("ball gag");
		curses.add("ball gravy");
		curses.add("ball kicking");
		curses.add("ball licking");
		curses.add("ball sack");
		curses.add("ball sucking");
		curses.add("bangbros");
		curses.add("bareback");
		curses.add("barely legal");
		curses.add("barenaked");
		curses.add("bastardo");
		curses.add("bastinado");
		curses.add("bbw");
		curses.add("bdsm");
		curses.add("beaner");
		curses.add("beaners");
		curses.add("beaver cleaver");
		curses.add("beaver lips");
		curses.add("big black");
		curses.add("big breasts");
		curses.add("big knockers");
		curses.add("big tits");
		curses.add("bimbos");
		curses.add("birdlock");
		curses.add("black cock");
		curses.add("blonde action");
		curses.add("blonde on blonde action");
		curses.add("blow your load");
		curses.add("blue waffle");
		curses.add("blumpkin");
		curses.add("bollocks");
		curses.add("bondage");
		curses.add("booty call");
		curses.add("brown showers");
		curses.add("brunette action");
		curses.add("bukkake");
		curses.add("bulldyke");
		curses.add("bullet vibe");
		curses.add("bung hole");
		curses.add("bunghole");
		curses.add("busty");
		curses.add(" butt ");
		curses.add(" butts ");
		curses.add("buttcheeks");
		curses.add("camel toe");
		curses.add("camgirl");
		curses.add("camslut");
		curses.add("camwhore");
		curses.add("carpetmuncher");
		curses.add("chocolate rosebuds");
		curses.add("circlejerk");
		curses.add("cleveland steamer");
		curses.add("clover clamps");
		curses.add("clusterfuck");
		curses.add("coprolagnia");
		curses.add("coprophilia");
		curses.add("cornhole");
		curses.add("coons");
		curses.add("creampie");
		curses.add("darkie");
		curses.add("date rape");
		curses.add("daterape");
		curses.add("deep throat");
		curses.add("deepthroat");
		curses.add("dendrophilia");
		curses.add("dingleberry");
		curses.add("dingleberries");
		curses.add("dirty pillows");
		curses.add("dirty sanchez");
		curses.add("doggie style");
		curses.add("doggiestyle");
		curses.add("doggy style");
		curses.add("doggystyle");
		curses.add("dog style");
		curses.add("dolcett");
		curses.add(" domination ");
		curses.add("dominatrix");
		curses.add("dommes");
		curses.add("donkey punch");
		curses.add("double dong");
		curses.add("double penetration");
		curses.add("dp action");
		curses.add("dry hump");
		curses.add("dvda");
		curses.add("eat my ass");
		curses.add("ecchi");
		curses.add("erotic");
		curses.add("erotism");

		curses.add("eunuch");
		curses.add("fecal");
		curses.add("felch");
		curses.add("feltch");
		curses.add("female squirting");
		curses.add("femdom");
		curses.add("figging");
		curses.add("fingerbang");
		curses.add("fingering");
		curses.add("fisting");
		curses.add("foot fetish");
		curses.add("footjob");
		curses.add("frotting");
		curses.add("fuck buttons");
		curses.add("fucktards");
		curses.add("futanari");
		curses.add("gang bang");
		curses.add("gay sex");
		curses.add("genitals");
		curses.add("giant cock");
		curses.add("girl on");
		curses.add("girl on top");
		curses.add("girls gone wild");
		curses.add("goatcx");
		curses.add("god damn");
		curses.add("gokkun");
		curses.add("golden shower");
		curses.add("goodpoop");
		curses.add("goo girl");
		curses.add("goregasm");
		curses.add("grope");
		curses.add("group sex");
		curses.add("g-spot");
		curses.add("guro");
		curses.add("hand job");
		curses.add("handjob");

		curses.add("hentai");
		curses.add("homoerotic");
		curses.add("honkey");
		curses.add("hooker");
		curses.add("hot carl");
		curses.add("hot chick");
		curses.add("how to kill");
		curses.add("how to murder");
		curses.add("huge fat");
		curses.add("humping");
		curses.add("incest");
		curses.add("intercourse");
		curses.add("jack off");
		curses.add("jail bait");
		curses.add("jailbait");
		curses.add("jelly donut");
		curses.add("jerk off");
		curses.add("jigaboo");
		curses.add("jiggaboo");
		curses.add("jiggerboo");
		curses.add("juggs");
		curses.add("kike");
		curses.add("kinbaku");
		curses.add("kinkster");
		curses.add("kinky");
		curses.add("knobbing");
		curses.add("leather restraint");
		curses.add("leather straight jacket");
		curses.add("lemon party");
		curses.add("lolita");
		curses.add("lovemaking");
		curses.add("make me come");
		curses.add("male squirting");
		curses.add("menage a trois");
		curses.add("milf");
		curses.add("missionary position");
		curses.add("mound of venus");
		curses.add("mr hands");
		curses.add("muff diver");
		curses.add("muffdiving");
		curses.add("nambla");
		curses.add("nawashi");
		curses.add("negro");
		curses.add("neonazi");
		curses.add("nig nog");
		curses.add("nimphomania");
		curses.add("nipple");
		curses.add("nipples");
		curses.add("nsfw images");
		curses.add("nude");
		curses.add("nudity");
		curses.add("nympho");
		curses.add("nymphomania");
		curses.add("octopussy");
		curses.add("omorashi");
		curses.add("one cup two girls");
		curses.add("one guy one jar");
		curses.add("orgy");
		curses.add("paedophile");
		curses.add("paki");
		curses.add("panties");
		curses.add("panty");
		curses.add("pedobear");
		curses.add("pedophile");
		curses.add("pegging");
		curses.add("phone sex");
		curses.add("piece of shit");
		curses.add("piss pig");
		curses.add("pisspig");
		curses.add("playboy");
		curses.add("pleasure chest");
		curses.add("pole smoker");
		curses.add("ponyplay");

		curses.add(" poon ");
		curses.add("poontang");
		curses.add("punany");
		curses.add("poop chute");
		curses.add("poopchute");
		curses.add("prince albert piercing");
		curses.add("pthc");
		curses.add("pubes");
		curses.add("queaf");
		curses.add("queef");
		curses.add("quim");
		curses.add("raghead");
		curses.add("raging boner");
		curses.add("raping");
		curses.add("rapist");
		curses.add("reverse cowgirl");
		curses.add("rimjob");
		curses.add("rosy palm");
		curses.add("rosy palm and her 5 sisters");
		curses.add("rusty trombone");
		curses.add("sadism");
		curses.add("santorum");
		curses.add("scat");
		curses.add("scissoring");
		curses.add("sexo");
		curses.add("sexy");
		curses.add("shaved beaver");
		curses.add("shaved pussy");
		curses.add("shibari");
		curses.add("shitblimp");
		curses.add("shota");
		curses.add("shrimping");
		curses.add("skeet");
		curses.add("slanteye");
		curses.add("s&m");
		curses.add("snowballing");
		curses.add("sodomize");
		curses.add("sodomy");
		curses.add(" spic ");
		curses.add("splooge");
		curses.add("splooge moose");
		curses.add("spooge");
		curses.add("spread legs");
		curses.add("strap on");
		curses.add("strapon");
		curses.add("strappado");
		curses.add("strip club");
		curses.add("style doggy");

		curses.add("suicide girls");
		curses.add("sultry women");
		curses.add("swastika");
		curses.add("swinger");
		curses.add("tainted love");
		curses.add("taste my");
		curses.add("tea bagging");
		curses.add("threesome");
		curses.add("throating");
		curses.add("tied up");
		curses.add("tight white");
		curses.add(" tit ");
		curses.add("titty");
		curses.add("tongue in a");
		curses.add("topless");
		curses.add("towelhead");
		curses.add("tranny");
		curses.add("tribadism");
		curses.add("tub girl");
		curses.add("tubgirl");
		curses.add("tushy");
		curses.add("twink");
		curses.add("twinkie");
		curses.add("two girls one cup");
		curses.add("undressing");
		curses.add("upskirt");
		curses.add("urethra play");
		curses.add("urophilia");
		curses.add("venus mound");
		curses.add("vibrator");
		curses.add("violet wand");
		curses.add("vorarephilia");
		curses.add("voyeur");
		curses.add("wetback");
		curses.add("wet dream");
		curses.add("white power");
		curses.add("wrapping men");
		curses.add("wrinkled starfish");

		curses.add(" xxx ");
		curses.add("yaoi");
		curses.add("yellow showers");
		curses.add("yiffy");
		curses.add("zoophilia");

		curses.add("tosser");

		curses.add(" kys ");
		curses.add("killyourself");
		curses.add("kill your self");
		curses.add("kill yourself");
		curses.add("killyour self");
		curses.add("killurself");
		curses.add("killurself");
		curses.add("kill urself");
		curses.add("k y s");
		curses.add("hypixel");
		curses.add("hypickle");
		curses.add("hyp1xel");
		curses.add("hypix3l");
		curses.add("mineplex");
		curses.add("cubecraft");

		curses.add("hive");
		curses.add("hivemc");
		curses.add("exoticraids");
		curses.add("play.");
		curses.add("pvp.");
		curses.add("mc.");
		curses.add(".net");
		curses.add(".nu");
		curses.add(".me");
		curses.add(".uk");
		curses.add(".org");
		curses.add("mc-");
		curses.add("us.");
		curses.add(".us");
		curses.add("pixel.");
		curses.add("jogar.");
		curses.add("gg.");
		curses.add(".gg");
		curses.add("na.");
		curses.add(".na");
		curses.add(".na");
		curses.add(".gs");
		curses.add(".eu");
		curses.add("sb-");
		curses.add(".cz");
		curses.add("play,");
		curses.add("pvp,");
		curses.add("mc,");
		curses.add(",net");
		curses.add(",nu");
		curses.add(",me");
		curses.add(",uk");
		curses.add(",org");
		curses.add("mc-");
		curses.add("us,");
		curses.add(",us");
		curses.add("pixel,");
		curses.add("jogar,");
		curses.add("gg,");
		curses.add(",gg");
		curses.add("na,");
		curses.add(",na");
		curses.add(",na");
		curses.add(",gs");
		curses.add(",eu");
		curses.add("sb-");
		curses.add(",cz");
		curses.add(".nu");

		curses.add("(dot)");
		curses.add("(.)");
		curses.add("[dot]");
		curses.add("{.}");
		curses.add("{dot}");
		curses.add(",com");
		curses.add("mc,");
		curses.add("[,]");
		curses.add(">>>");
		curses.add("<<<");
		curses.add("mc_");
		curses.add("_net");
	}

}
