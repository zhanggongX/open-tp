package cn.opentp.core.thread.pool;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolWrapper {

    /**
     * 线程池状态信息
     */
    private final ThreadPoolState state = new ThreadPoolState();

    /**
     * 目标线程池
     */
    private ThreadPoolExecutor target;

    public ThreadPoolWrapper() {
    }

    public ThreadPoolWrapper(ThreadPoolExecutor target) {
        this.target = target;
    }

    public ThreadPoolState getState() {
        return state;
    }

    public ThreadPoolExecutor getTarget() {
        return target;
    }

    public void setTarget(ThreadPoolExecutor target) {
        this.target = target;
    }

    public void flushStateAndSetThreadPoolName(String threadPoolName) {
        state.setThreadPoolName(threadPoolName);
        if (target == null) {
            state.flushDefault();
            return;
        }
        state.setCoreSize(target.getCorePoolSize());
        state.setMaxSize(target.getMaximumPoolSize());
        state.setPoolSize(target.getPoolSize());
        state.setActiveCount(target.getActiveCount());
        state.setCompletedCount(target.getCompletedTaskCount());
        state.setQueueSize(target.getQueue().size());
        state.setQueueLength(target.getQueue().remainingCapacity());
        state.setLargestPoolSize(target.getLargestPoolSize());
    }

    public void flushTargetState(ThreadPoolState threadPoolState) {
        if (target == null) {
            return;
        }
        // todo 暂时先支持修改核心线程数和最大线程数
        target.setCorePoolSize(threadPoolState.getCoreSize() != -1 ? threadPoolState.getCoreSize() : target.getCorePoolSize());
        target.setMaximumPoolSize(threadPoolState.getMaxSize() != -1 ? threadPoolState.getMaxSize() : target.getMaximumPoolSize());
    }
}
