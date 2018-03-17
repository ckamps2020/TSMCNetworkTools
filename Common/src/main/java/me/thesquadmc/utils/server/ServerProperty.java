package me.thesquadmc.utils.server;

import java.util.function.Predicate;

/**
 * Represents a default server property found within the {@code server.properties} file
 */
public final class ServerProperty<T> {

	public static final ServerProperty<Integer> SPAWN_PROTECTION = new ServerProperty<>(Integer.class, "spawn-protection", 16);
	public static final ServerProperty<String> SERVER_NAME = new ServerProperty<>(String.class, "server-name", "");
	public static final ServerProperty<String> GENERATOR_SETTINGS = new ServerProperty<>(String.class, "generator-settings", "");
	public static final ServerProperty<Boolean> FORCE_GAMEMODE = new ServerProperty<>(Boolean.class, "force-gamemode", false);
	public static final ServerProperty<Boolean> ALLOW_NETHER = new ServerProperty<>(Boolean.class, "allow-nether", true);
	public static final ServerProperty<Integer> GAMEMODE = new ServerProperty<>(Integer.class, "gamemode", 0, i -> (i >= 0 && i <= 3));
	public static final ServerProperty<Boolean> BROADCAST_CONSOLE_TO_OPS = new ServerProperty<>(Boolean.class, "broadcast-console-to-ops", true);
	public static final ServerProperty<Boolean> ENABLE_QUERY = new ServerProperty<>(Boolean.class, "enable-query", false);
	public static final ServerProperty<Integer> PLAYER_IDLE_TIMEOUT = new ServerProperty<>(Integer.class, "player-idle-timeout", 0);
	public static final ServerProperty<Integer> DIFFICULTY = new ServerProperty<>(Integer.class, "difficulty", 1, i -> (i >= 0 && i <= 3));
	public static final ServerProperty<Boolean> SPAWN_MONSTERS = new ServerProperty<>(Boolean.class, "spawn-monsters", true);
	public static final ServerProperty<Integer> OP_PERMISSION_LEVEL = new ServerProperty<>(Integer.class, "op-permission-level", 4, i -> (i >= 1 && i <= 4));
	public static final ServerProperty<Boolean> PVP = new ServerProperty<>(Boolean.class, "pvp", true);
	public static final ServerProperty<Boolean> SNOOPER_ENABLED = new ServerProperty<>(Boolean.class, "snooper-enabled", true);
	public static final ServerProperty<String> LEVEL_TYPE = new ServerProperty<>(String.class, "level-type", "DEFAULT");
	public static final ServerProperty<Boolean> HARDCORE = new ServerProperty<>(Boolean.class, "hardcore", false);
	public static final ServerProperty<Boolean> ENABLE_COMMAND_BLOCKS = new ServerProperty<>(Boolean.class, "enable-command-blocks", true);
	public static final ServerProperty<Integer> MAX_PLAYERS = new ServerProperty<>(Integer.class, "max-players", 20, i -> i >= 0);
	public static final ServerProperty<Integer> NETWORK_COMPRESSION_THRESHOLD = new ServerProperty<>(Integer.class, "network-compression-threshold", 256, i -> i >= -1);
	public static final ServerProperty<String> RESOURCE_PACK_SHA1 = new ServerProperty<>(String.class, "resource-pack-sha1", "");
	public static final ServerProperty<Integer> MAX_WORLD_SIZE = new ServerProperty<>(Integer.class, "max-world-size", 29999984, i -> (i >= 1 && i <= 29999984));
	public static final ServerProperty<Integer> SERVER_PORT = new ServerProperty<>(Integer.class, "server-port", 25565);
	public static final ServerProperty<Boolean> DEBUG = new ServerProperty<>(Boolean.class, "debug", false);
	public static final ServerProperty<String> SERVER_IP = new ServerProperty<>(String.class, "server-ip", "");
	public static final ServerProperty<Boolean> SPAWN_NPCS = new ServerProperty<>(Boolean.class, "spawn-npcs", true);
	public static final ServerProperty<Boolean> ALLOW_FLIGHT = new ServerProperty<>(Boolean.class, "allow-flight", false);
	public static final ServerProperty<String> LEVEL_NAME = new ServerProperty<>(String.class, "level-name", "world");
	public static final ServerProperty<Integer> VIEW_DISTANCE = new ServerProperty<>(Integer.class, "view-distance", 10, i -> (i >= 2 && i <= 32));
	public static final ServerProperty<String> RESOURCE_PACK = new ServerProperty<>(String.class, "resource-pack", "");
	public static final ServerProperty<Boolean> SPAWN_ANIMALS = new ServerProperty<>(Boolean.class, "spawn-animals", true);
	public static final ServerProperty<Boolean> WHITE_LIST = new ServerProperty<>(Boolean.class, "white-list", false);
	public static final ServerProperty<Boolean> GENERATE_STRUCTURES = new ServerProperty<>(Boolean.class, "generate-structures", true);
	public static final ServerProperty<Boolean> ONLINE_MODE = new ServerProperty<>(Boolean.class, "online-mode", true);
	public static final ServerProperty<Integer> MAX_BUILD_HEIGHT = new ServerProperty<>(Integer.class, "max-build-height", 256);
	public static final ServerProperty<String> LEVEL_SEED = new ServerProperty<>(String.class, "level-seed", "");
	public static final ServerProperty<Boolean> PREVENT_PROXY_CONNECTIONS = new ServerProperty<>(Boolean.class, "prevent-proxy-connections", false);
	public static final ServerProperty<String> MOTD = new ServerProperty<>(String.class, "motd", "A Minecraft Server");
	public static final ServerProperty<Boolean> ENABLE_RCON = new ServerProperty<>(Boolean.class, "enable-rcon", false);


	private final Class<T> type;
	private final String propertyName;
	private final T defaultValue;
	private final Predicate<T> valueValidation;

	private ServerProperty(Class<T> type, String propertyName, T defaultValue, Predicate<T> valueValidation) {
		this.type = type;
		this.propertyName = propertyName;
		this.defaultValue = defaultValue;
		this.valueValidation = valueValidation;
	}

	private ServerProperty(Class<T> type, String propertyName, T defaultValue) {
		this(type, propertyName, defaultValue, v -> true);
	}

	/**
	 * Get the type of property represented by this server property
	 * 
	 * @return the property type
	 */
	public Class<T> getType() {
		return this.type;
	}

	/**
	 * Get the name of this property as found within the {@code server.properties} file
	 * 
	 * @return the property name
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Get this property's default value as generated by the vanilla Minecraft server
	 * 
	 * @return the default value
	 */
	public T getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Check whether the provided value is valid for this property. Some properties impose
	 * limitations on acceptable values (generally integer-based properties), though for those
	 * that do not, this method will always return true
	 * 
	 * @param value the value to check
	 * @return true if valid property value, false otherwise
	 */
	public boolean isValidValue(T value) {
		return valueValidation.test(value);
	}

}
