package me.thesquadmc.networking.mysql;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	public MySQL(Plugin plugin, String _hostname, String _port, String _database, String username, String _password) {
		super(plugin);
		this.hostname = _hostname;
		this.port = _port;
		this.database = _database;
		this.user = username;
		this.password = _password;
	}

	public Connection openConnection()
			throws SQLException, ClassNotFoundException {
		if (checkConnection()) {
			return this.connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database,
				this.user, this.password);
		return this.connection;
	}

}
