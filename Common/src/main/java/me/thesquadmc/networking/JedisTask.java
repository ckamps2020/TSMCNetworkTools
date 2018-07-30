package me.thesquadmc.networking;

import com.google.gson.Gson;
import me.thesquadmc.Main;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class JedisTask {

	private static final Gson gson = Main.getMain().getGson();
	private Map<String, Object> data;
	private String task;
	private String channel;

	private JedisTask(String name) {
		this.task = name;
		this.data = new HashMap<>();
	}

	public static JedisTask withName(String name) {
		return new JedisTask(name);
	}

	public JedisTask withArg(String arg, Object o) {
		this.data.put(arg, o);
		return this;
	}

	public boolean send(String channel, Jedis jedis) {
		this.channel = channel;

		if (jedis.isConnected()) {
			jedis.publish(channel, gson.toJson(this));
		}
		return true;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getChannel() {
		return channel;
	}

}
