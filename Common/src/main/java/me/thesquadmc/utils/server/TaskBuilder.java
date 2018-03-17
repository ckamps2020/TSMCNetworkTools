package me.thesquadmc.utils.server;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class TaskBuilder extends BukkitRunnable {

	/**
	 * Instance to our JavaPlugin
	 **/
	private JavaPlugin plugin;

	/**
	 * The type of task (ASYNC or SYNC)
	 **/
	private TaskBuilder.TaskType type;

	/**
	 * Runnable that is executed
	 **/
	private Runnable runnable;

	/**
	 * The delay before the runnable is executed
	 **/
	private long delay = 0L;

	/**
	 * Amount of time to wait in between executions
	 **/
	private long interval = 0L;

	/**
	 * Amount of times to run the runnable
	 *
	 * Set to -1 for infinite
	 **/
	private int cycles = 0;

	/**
	 * The BukkitTask that is created
	 **/
	private BukkitTask task;

	/**
	 * Runnable that is executed once all cycles are complete
	 **/
	private Runnable onComplete;

	/**
	 * Internal counter for how many cycles have executed
	 **/
	private int count = 0;

	/**
	 * Builds a synchronous task
	 * @param plugin instance to our plugin
	 */
	public static TaskBuilder sync(JavaPlugin plugin) {
		return builder().type(TaskBuilder.TaskType.SYNC).plugin(plugin).build();
	}

	/**
	 * Builds a asynchronous task
	 * @param plugin instance to our plugin
	 */
	public static TaskBuilder async(JavaPlugin plugin) {
		return builder().type(TaskBuilder.TaskType.ASYNC).plugin(plugin).build();
	}

	public static TaskBuilder builder() {
		return new TaskBuilder();
	}

	public TaskBuilder type(TaskBuilder.TaskType type) {
		this.type = type;
		return this;
	}

	public TaskBuilder plugin(JavaPlugin plugin) {
		this.plugin = plugin;
		return this;
	}

	public TaskBuilder interval(long interval) {
		this.interval = interval;
		return this;
	}

	public TaskBuilder delay(long delay) {
		this.delay = delay;
		return this;
	}

	public TaskBuilder cycles(int cycles) {
		this.cycles = cycles;
		return this;
	}

	public TaskBuilder build() {
		return this;
	}

	public long getDelay() {
		return delay;
	}

	public long getInterval() {
		return interval;
	}

	public long getCycles() {
		return cycles;
	}

	public TaskBuilder onComplete(Runnable runnable) {
		onComplete = runnable;
		return this;
	}

	public BukkitTask run(Runnable runnable) {
		this.runnable = runnable;

		if (interval == 0L && cycles == 0) {
			if (delay == 0L) {
				if (type == TaskBuilder.TaskType.SYNC) {
					return task = runTask(plugin);
				}

				if (type == TaskBuilder.TaskType.ASYNC) {
					return task = runTaskAsynchronously(plugin);
				}

			} else {
				if (type == TaskBuilder.TaskType.SYNC) {
					return task = runTaskLater(plugin, delay);
				}

				if (type == TaskBuilder.TaskType.ASYNC) {
					return task = runTaskLaterAsynchronously(plugin, delay);
				}
			}

		} else {
			if (type == TaskBuilder.TaskType.SYNC) {
				return task = runTaskTimer(plugin, delay, interval);
			}

			if (type == TaskBuilder.TaskType.ASYNC) {
				return task = runTaskTimerAsynchronously(plugin, delay, interval);
			}
		}

		return null;
	}

	public void run() {
		runnable.run();
		++count;

		if (cycles > 0 && count >= cycles) {
			task.cancel();

			if (onComplete != null) {
				onComplete.run();
			}
		}

	}

	public enum TaskType {
		SYNC,
		ASYNC
	}

}
