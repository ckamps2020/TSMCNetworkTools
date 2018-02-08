package me.thesquadmc.networking.mysql;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

	protected Connection connection;
	protected Plugin plugin;

	protected Database(Plugin _plugin) {
		this.plugin = _plugin;
		this.connection = null;
	}

	public abstract Connection openConnection()
			throws SQLException, ClassNotFoundException;

	public boolean checkConnection()
			throws SQLException {
		return (this.connection != null) && (!this.connection.isClosed());
	}

	public Connection getConnection() {
		return this.connection;
	}

	public boolean closeConnection()
			throws SQLException {
		if (this.connection == null) {
			return false;
		}
		this.connection.close();
		return true;
	}

}
