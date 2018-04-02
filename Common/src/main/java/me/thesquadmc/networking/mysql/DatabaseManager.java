package me.thesquadmc.networking.mysql;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.Settings;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public final class DatabaseManager {

	private Main main;
	private Plugin plugin;
	public MySQL DB;

	public DatabaseManager(Main main, Plugin plugin) {
		this.main = main;
		this.plugin = plugin;
	}

	public MySQL getDB() {
		return DB;
	}

	public void setupDB() throws SQLException, ClassNotFoundException {
		this.DB = new MySQL(plugin, main.getMysqlhost(), main.getMysqlport(), main.getMysqldb(), main.getDbuser(), main.getMysqlpassword());
		this.DB.openConnection();

		Statement a = this.DB.getConnection().createStatement();
		a.executeUpdate("CREATE TABLE IF NOT EXISTS `DISGUISE` (`UUID` MEDIUMTEXT, `DISGUISENAME` MEDIUMTEXT);");
		a.executeUpdate("CREATE TABLE IF NOT EXISTS `FRIENDS` (`UUID` MEDIUMTEXT, `FRIENDS` LONGTEXT);");
		a.executeUpdate("CREATE TABLE IF NOT EXISTS `REMOVAL` (`UUID` MEDIUMTEXT, `REMOVAL` LONGTEXT);");
		a.executeUpdate("CREATE TABLE IF NOT EXISTS `SETTINGS` (`UUID` MEDIUMTEXT, `NOTIFICATIONS` BOOLEAN, `PMS` BOOLEAN, `FRIENDCHAT` BOOLEAN, `REQUESTS` BOOLEAN);");
		System.out.println("[NetworkTools] Tables created/loaded!");
	}

	public void closeConnection() throws SQLException, ClassNotFoundException {
		this.DB.closeConnection();
	}

	public int getTotalUniqueAccounts() throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement s = this.DB.getConnection().createStatement();
			ResultSet resultSet = s.executeQuery("SELECT COUNT(*) FROM FRIENDS");
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void saveFriendAccount(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			List<String> friends = main.getFriends().get(UUID.fromString(uuid));
			StringBuilder stringBuilder = new StringBuilder();
			if (friends == null) {
				return;
			}
			for (String s : friends) {
				if (s.equalsIgnoreCase("[]") || s.equalsIgnoreCase(" ") || s.equalsIgnoreCase("")) {
					continue;
				}
				stringBuilder.append(s + " ");
			}
			if (friends.isEmpty()) {
				Statement s = this.DB.getConnection().createStatement();
				s.executeUpdate("UPDATE `FRIENDS` SET `FRIENDS` = 'NONE' WHERE `UUID` = '" + uuid + "'");
			} else {
				Statement s = this.DB.getConnection().createStatement();
				s.executeUpdate("UPDATE `FRIENDS` SET `FRIENDS` = '" + stringBuilder.toString() + "' WHERE `UUID` = '" + uuid + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateDisguise(String uuid, String name) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement s = this.DB.getConnection().createStatement();
			s.executeUpdate("UPDATE `DISGUISE` SET `DISGUISENAME` = '" + name + "' WHERE `UUID` = '" + uuid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newDisguiseAccount(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement s = this.DB.getConnection().createStatement();
			s.executeUpdate("INSERT INTO `DISGUISE` (`UUID`, `DISGUISENAME`) VALUES ('" + uuid + "', 'NONE');");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateSettings(Settings settings, int value, String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement s = this.DB.getConnection().createStatement();
			s.executeUpdate("UPDATE `SETTINGS` SET `" + settings.name() + "` = '" + value + "' WHERE `UUID` = '" + uuid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFriendAccount(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			String f = "NONE";
			if (getFriends(uuid) != null) {
				f = getFriends(uuid);
			} else {
				newFriendAccount(uuid);
			}
			if (f.equalsIgnoreCase("NONE")) {
				main.getFriends().put(UUID.fromString(uuid), new ArrayList<>());
				main.getSettings().put(UUID.fromString(uuid), getSettings(uuid));
			} else {
				List<String> friends = new ArrayList<>();
				String regex = "[ ]+";
				String[] tokens = f.split(regex);
				for (String s : tokens) {
					friends.add(s);
				}
				List<String> remove = new ArrayList<>();
				String r = "[ ]+";
				String[] t = getRemovals(uuid).split(r);
				if (t != null) {
					if (t.length == 1) {
						if (t[0].equalsIgnoreCase("NONE")) {
							//continue
						} else {
							for (String l : friends) {
								for (String lol : t) {
									if (l.equalsIgnoreCase(lol)) {
										remove.add(l);
									}
								}
							}
						}
					} else {
						for (String l : friends) {
							for (String lol : t) {
								if (l.equalsIgnoreCase(lol)) {
									remove.add(l);
								}
							}
						}
					}
				}
				if (!remove.isEmpty()) {
					for (String s : remove) {
						friends.remove(s);
					}
				}
				resetRemovals(uuid);
				main.getFriends().put(UUID.fromString(uuid), friends);
				main.getSettings().put(UUID.fromString(uuid), getSettings(uuid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newRemoval(String uuid, String removalUUID) throws Exception {
		if (getRemovals(uuid) != null) {
			String r = "[ ]+";
			String[] t = getRemovals(uuid).split(r);
			List<String> string = new ArrayList<>();
			for (String tt : t) {
				if (tt.equalsIgnoreCase("NONE")) {
					continue;
				}
				string.add(tt);
			}
			string.add(removalUUID);
			StringBuilder stringBuilder = new StringBuilder();
			for (String s : string) {
				stringBuilder.append(s + " ");
			}
			if (!this.DB.checkConnection()) {
				this.DB.openConnection();
			}
			try {
				Statement s = this.DB.getConnection().createStatement();
				s.executeUpdate("UPDATE `REMOVAL` SET `REMOVAL` = '" + stringBuilder.toString() + "' WHERE `UUID` = '" + uuid + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (!this.DB.checkConnection()) {
				this.DB.openConnection();
			}
			try {
				Statement s = this.DB.getConnection().createStatement();
				s.executeUpdate("UPDATE `REMOVAL` SET `REMOVAL` = '" + removalUUID + "' WHERE `UUID` = '" + uuid + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void newFriendAccount(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement s = this.DB.getConnection().createStatement();
			s.executeUpdate("INSERT INTO `FRIENDS` (`UUID`, `FRIENDS`) VALUES ('" + uuid + "', 'NONE');");
			Statement ss = this.DB.getConnection().createStatement();
			ss.executeUpdate("INSERT INTO `REMOVAL` (`UUID`, `REMOVAL`) VALUES ('" + uuid + "', 'NONE');");
			Statement sss = this.DB.getConnection().createStatement();
			sss.executeUpdate("INSERT INTO `SETTINGS` (`UUID`, `NOTIFICATIONS`, `PMS`, `FRIENDCHAT`, `REQUESTS`) VALUES ('" + uuid + "', '1', '1', '0', '1');");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFriends(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement s = this.DB.getConnection().createStatement();
			ResultSet resultSet = s.executeQuery("SELECT * FROM `FRIENDS` WHERE `UUID`='" + uuid + "';");
			if (resultSet.next()) {
				String msg = resultSet.getString("FRIENDS");
				String a = msg.replaceAll("\\[", "");
				String b = a.replaceAll("\\]", "");
				return b.replaceAll(",", "");
			} else {
				return null;
			}
		} catch (Exception e) {
			return "NONE";
		}
	}

	public Map<Settings, Boolean> getSettings(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement ss = this.DB.getConnection().createStatement();
			ResultSet resultSet = ss.executeQuery("SELECT * FROM `SETTINGS` WHERE `UUID`='" + uuid + "';");
			Map<Settings, Boolean> settings = new HashMap<>();
			if (resultSet.next()) {
				settings.put(Settings.NOTIFICATIONS, resultSet.getBoolean("NOTIFICATIONS"));
				settings.put(Settings.PMS, resultSet.getBoolean("PMS"));
				settings.put(Settings.FRIENDCHAT, resultSet.getBoolean("FRIENDCHAT"));
				settings.put(Settings.REQUESTS, resultSet.getBoolean("REQUESTS"));
			}
			settings.put(Settings.SOCIALSPY, false);
			return settings;
		} catch (Exception e) {
			return null;
		}
	}

	public String getRemovals(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement ss = this.DB.getConnection().createStatement();
			ResultSet resultSet = ss.executeQuery("SELECT * FROM `REMOVAL` WHERE `UUID`='" + uuid + "';");
			if (resultSet.next()) {
				return resultSet.getString("REMOVAL");
			} else {
				return null;
			}
		} catch (Exception e) {
			return "NONE";
		}
	}

	public void resetRemovals(String uuid) throws Exception {
		if (!this.DB.checkConnection()) {
			this.DB.openConnection();
		}
		try {
			Statement ss = this.DB.getConnection().createStatement();
			ss.executeUpdate("UPDATE `REMOVAL` SET `REMOVAL` = 'NONE' WHERE `UUID` = '" + uuid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}