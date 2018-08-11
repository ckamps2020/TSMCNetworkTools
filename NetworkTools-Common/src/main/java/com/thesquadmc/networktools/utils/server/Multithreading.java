package com.thesquadmc.networktools.utils.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Multithreading {

    public static ExecutorService POOL = Executors.newCachedThreadPool();

    public static void runAsync(Runnable runnable) {
        runAsync(runnable, true);
    }

    public static void runAsync(Runnable runnable, boolean strict) {
        if (strict)
            POOL.execute(runnable);
        else {
            if (isMainThread()) {
                POOL.execute(runnable);

            } else
                runnable.run();
        }

    }

    public static boolean isMainThread() {
        return Thread.currentThread().getName().equals("main") || Thread.currentThread().getName().equals("Server thread");
    }

    public static void runNewThread(String threadName, Runnable runnable) {
        new Thread(runnable, threadName).start();
    }

}
