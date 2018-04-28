package me.thesquadmc.buycraft;

import me.thesquadmc.buycraft.data.*;
import me.thesquadmc.buycraft.data.Package;
import me.thesquadmc.utils.json.JSONReader;
import me.thesquadmc.utils.json.JSONUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Buycraft {

	private static String url;
	private String secret;

	public Buycraft(String secret) throws BuycraftException {
		if (secret == null || secret.length() != 40){
			throw new BuycraftException("The secret key is not valid");
		}

		setSecure(true);
		this.secret = secret;
	}

	private JSONObject jsonGet(String path){
		try {
			return JSONReader.readJsonFromUrl(url + path, secret, false);
		} catch (IOException ignored) {}

		return null;
	}

	private JSONObject jsonArrayGet(String path){
		try {
			return JSONReader.readJsonFromUrl(url + path, secret, true);
		} catch (IOException ignored){}

		return null;
	}

	public Information getInformation() {
		try {
			JSONObject obj = jsonGet("/information");

			checkError(obj);

			JSONObject account = JSONUtils.safeGetObject(obj, "account");
			JSONObject currency = JSONUtils.safeGetObject(account, "currency");

			JSONObject serverObj = JSONUtils.safeGetObject(obj, "server");

			int serverId = JSONUtils.safeGetInt(serverObj, "id");
			String serverName = JSONUtils.safeGetString(serverObj, "name");

			JSONObject analytics = JSONUtils.safeGetObject(JSONUtils.safeGetObject(obj, "analytics"), "internal");

			String projectAna = JSONUtils.safeGetString(analytics, "project");
			String anaKey = JSONUtils.safeGetString(analytics, "key");

			return new Information(new Account(account, currency), serverId, serverName, projectAna, anaKey);
		} catch (BuycraftException e){
			e.printStackTrace();
		}

		return null;
	}

	public Set<Category> getListing(){
		try {
			JSONObject obj = jsonGet("/listing");
			checkError(obj);

			JSONArray catArray = JSONUtils.safeGetArray(obj, "categories");
			return loopCategory(catArray);
		} catch (BuycraftException e){
			e.printStackTrace();
		}

		return null;
	}


	private Set<Category> loopCategory(JSONArray array){
		if (array==null){
			return new HashSet<>();
		}
		Set<Category> categories = new HashSet<>();

		for (int i = 0; i<array.length(); i++){
			JSONObject object = JSONUtils.safeGetObject(array, i);
			Category category = parseCategory(object);
			categories.add(category);
		}

		return categories;
	}

	private Category parseCategory(JSONObject obj){
		int id = JSONUtils.safeGetInt(obj, "id");
		int order = JSONUtils.safeGetInt(obj, "order");
		String name = JSONUtils.safeGetString(obj, "name");
		Set<Category> childs = loopCategory(JSONUtils.safeGetArray(obj, "subcategories"));
		Set<Package> packages = parsePackages(obj);

		return new Category(id, order, name, childs, packages);
	}

	private Set<Package> parsePackages(JSONObject object){
		JSONArray array = JSONUtils.safeGetArray(object, "packages");
		Set<Package> packages = new HashSet<>();
		if (array==null){
			return packages;
		}

		for (int i = 0; i < array.length(); i++){
			packages.add(parsePackage(JSONUtils.safeGetObject(array, i)));
		}

		return packages;
	}

	private Package parsePackage(JSONObject object){
		int id = JSONUtils.safeGetInt(object, "id");
		int order = JSONUtils.safeGetInt(object, "order");
		String name = JSONUtils.safeGetString(object, "name");
		double price = Double.parseDouble(JSONUtils.safeGetString(object, "price"));

		JSONObject sale = JSONUtils.safeGetObject(object, "sale");

		boolean saleActive = JSONUtils.safeGetBoolean(sale, "active");
		double discount = Double.parseDouble(JSONUtils.safeGetString(sale, "discount"));

		return new Package(id, order, name, price, saleActive, discount);
	}

	public PlayerQueue getQueue(int page){
		try {
			JSONObject obj = jsonGet("/queue?page=" + page);
			checkError(obj);

			JSONObject meta = JSONUtils.safeGetObject(obj, "meta");

			return new PlayerQueue(JSONUtils.safeGetBoolean(meta, "execute_offline"), JSONUtils.safeGetInt(obj, "next_check"), JSONUtils.safeGetBoolean(obj, "more"));
		} catch (BuycraftException e){
			e.printStackTrace();
		}

		return null;
	}

	public Set<OfflineCommand> getOfflineCommands(){
		try {
			JSONObject obj = jsonGet("/queue/offline-commands");
			checkError(obj);

			JSONArray array = JSONUtils.safeGetArray(obj, "commands");
			Set<OfflineCommand> offlineCommands = new HashSet<>();

			for (int i = 0; i < (array==null ? 0 : array.length()); i++){
				offlineCommands.add(parseOfflineCmd(JSONUtils.safeGetObject(array, i)));
			}

			return offlineCommands;
		} catch (BuycraftException e){
			e.printStackTrace();
		}

		return null;
	}

	private OfflineCommand parseOfflineCmd(JSONObject obj){
		int id = JSONUtils.safeGetInt(obj, "id");
		String cmd = JSONUtils.safeGetString(obj, "command");
		String payment = JSONUtils.safeGetString(obj, "payment");
		String packageId = JSONUtils.safeGetString(obj, "package");
		int delay = JSONUtils.safeGetInt(JSONUtils.safeGetObject(obj, "conditions"), "delay");

		JSONObject player = JSONUtils.safeGetObject(obj, "player");
		int playerId = JSONUtils.safeGetInt(player, "id");
		String playerName = JSONUtils.safeGetString(player, "name");

		String uuidString = JSONUtils.safeGetString(player, "uuid");
		UUID uuid = uuidString==null||uuidString.isEmpty() ? null : UUID.fromString(uuidString);

		return new OfflineCommand(id, cmd, payment, packageId, delay, playerId, playerName, uuid);
	}

	public Set<Payment> getLatestPayments(int limit){
		try {
			JSONObject obj = jsonArrayGet("/payments" + (limit == -1 ? "" : "?limit=" + limit));
			checkError(obj);

			JSONArray payments = JSONUtils.safeGetArray(obj, "main");
			Set<Payment> paymentSet = new HashSet<>();

			for (int i = 0; i<(payments==null ? 0 : payments.length()); i++){
				paymentSet.add(parsePayment(JSONUtils.safeGetObject(payments, i)));
			}

			return paymentSet;
		} catch (BuycraftException e){
			e.printStackTrace();
		}

		return null;
	}

	private Payment parsePayment(JSONObject obj){
		int id = JSONUtils.safeGetInt(obj, "id");
		double amount = Double.parseDouble(JSONUtils.safeGetString(obj, "amount"));

		Date date = TimeUtils.parseDate(JSONUtils.safeGetString(obj, "date"));

		JSONObject currencyObj = JSONUtils.safeGetObject(obj, "currency");
		String currency = JSONUtils.safeGetString(currencyObj, "iso_4217");
		String currencySymbol = JSONUtils.safeGetString(currencyObj, "symbol");

		JSONObject playerObj = JSONUtils.safeGetObject(obj, "player");
		int playerId = JSONUtils.safeGetInt(playerObj, "id");
		String playerName = JSONUtils.safeGetString(playerObj, "name");

		String uuidString = JSONUtils.safeGetString(playerObj, "uuid");
		UUID uuid = parseUuid(uuidString);

		JSONArray packages = JSONUtils.safeGetArray(obj, "packages");

		return new Payment(id, amount, date, currency, currencySymbol, playerId, playerName, uuid, getBoughtPackages(packages));
	}

	private UUID parseUuid(String text) {
		if (text == null || text.isEmpty()) {
			return null;
		}

		text = text.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");

		return UUID.fromString(text);
	}

	private Map<Integer, String> getBoughtPackages(JSONArray array){
		Map<Integer, String> map = new HashMap<>();
		for (int i = 0; i<(array==null ? 0 : array.length()); i++){
			JSONObject obj = JSONUtils.safeGetObject(array, i);
			map.put(JSONUtils.safeGetInt(obj, "id"), JSONUtils.safeGetString(obj, "name"));
		}

		return map;
	}

	private void checkError(JSONObject obj) throws BuycraftException {
		if (obj == null){
			throw new BuycraftException("Couldn't connect to the Buycraft API");
		}
		if (JSONUtils.safeGetInt(obj, "error_code", false)!=null){
			throw new BuycraftException(JSONUtils.safeGetString(obj, "error_message"));
		}
	}

	public void setSecure(boolean secure){
		url = "http" + (secure ? "s" : "") + "://plugin.buycraft.net";
	}

	public static <T> T filterAndGet(Stream<T> stream, Predicate<T> predicate){
		try {
			return stream.filter(predicate).findFirst().get();
		} catch (NoSuchElementException e){
			return null;
		}
	}

}
