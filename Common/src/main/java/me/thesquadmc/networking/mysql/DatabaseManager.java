package me.thesquadmc.networking.mysql;

import me.thesquadmc.Main;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

	public void closeConnection() throws SQLException {
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

}
