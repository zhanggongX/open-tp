package cn.opentp.core.thread.pool;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolContext implements Serializable {

    /**
     * 线程池状态信息
     */
    private final ThreadPoolState state = new ThreadPoolState();

    /**
     * 目标线程池
     */
    private ThreadPoolExecutor target;

    public ThreadPoolContext() {
    }

    public ThreadPoolContext(ThreadPoolExecutor target) {
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

    public void flushState(String threadPoolName) {
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

    public void flushTarget(ThreadPoolState threadPoolState) {
        if (target == null) {
            return;
        }
        // todo 暂时先支持修改核心线程数和最大线程数
        target.setCorePoolSize(threadPoolState.getPoolSize() > 0 ? threadPoolState.getPoolSize() : target.getCorePoolSize());
        target.setMaximumPoolSize(threadPoolState.getMaxSize() > 0 ? threadPoolState.getMaxSize() : target.getMaximumPoolSize());
    }
}
