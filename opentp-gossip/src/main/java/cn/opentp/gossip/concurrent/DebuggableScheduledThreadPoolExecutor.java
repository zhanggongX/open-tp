package cn.opentp.gossip.concurrent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DebuggableScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public DebuggableScheduledThreadPoolExecutor(int corePoolSize, String threadPoolName, int priority) {
        super(corePoolSize, new NamedThreadFactory(threadPoolName, priority));
    }

    public DebuggableScheduledThreadPoolExecutor(String threadPoolName) {
        this(1, threadPoolName, Thread.NORM_PRIORITY);
    }

    // We need this as well as the wrapper for the benefit of non-repeating tasks
    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        DebuggableThreadPoolExecutor.logExceptionsAfterExecute(r, t);
    }

    // override scheduling to supress exceptions that would cancel future executions
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(new UncomplainingRunnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(new UncomplainingRunnable(command), initialDelay, delay, unit);
    }

    private static class UncomplainingRunnable implements Runnable {
        private final Runnable runnable;

        public UncomplainingRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            try {
                runnable.run();
            } catch (Throwable e) {
                DebuggableThreadPoolExecutor.handleOrLog(e);
            }
        }
    }
}
