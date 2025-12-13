package top.bogey.touch_tool.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.utils.thread.TaskQueue;
import top.bogey.touch_tool.utils.thread.TaskThreadPoolExecutor;

public class ThreadUtil {
    private static final ExecutorService executorService = new TaskThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS, new TaskQueue<>(10));
    private static final ExecutorService taskService = new TaskThreadPoolExecutor(5, 30, 60, TimeUnit.SECONDS, new TaskQueue<>(20));

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static Future<?> submitTask(Runnable runnable) {
        return taskService.submit(runnable);
    }
}