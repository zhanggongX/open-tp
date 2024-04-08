package cn.opentp.core.tp;

import java.beans.Transient;
import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolWrapper implements Serializable {

    /**
     * 核心线程数
     */
    private int coreSize;
    /***
     * 最大线程数
     */
    private int maxSize;
    /**
     * 当前线程数
     */
    private int poolSize;
    /**
     * 活跃线程数
     */
    private int activeCount;
    /**
     * 已完成线程数
     */
    private long completedCount;
    /**
     * 队列大小
     */
    private int queueSize;
    /**
     * 队列长度
     */
    private int queueLength;
    /**
     * 执行过的最大线程数
     */
    private int largestPoolSize;
    /**
     * 线程池
     */
    private transient ThreadPoolExecutor target;

    public ThreadPoolWrapper() {
    }

    public ThreadPoolWrapper(ThreadPoolExecutor target) {
        this.target = target;
    }

    public void flush() {
        if (target == null) {
            setDefault();
        }
        this.coreSize = target.getCorePoolSize();
        this.maxSize = target.getMaximumPoolSize();
        this.poolSize = target.getPoolSize();
        this.activeCount = target.getActiveCount();
        this.completedCount = target.getCompletedTaskCount();
        this.queueSize = target.getQueue().size();
        this.queueLength = target.getQueue().remainingCapacity();
        this.largestPoolSize = target.getLargestPoolSize();
    }

    public void setDefault() {
        this.coreSize = -1;
        this.maxSize = -1;
        this.poolSize = -1;
        this.activeCount = -1;
        this.completedCount = -1;
        this.queueSize = -1;
        this.queueLength = -1;
        this.largestPoolSize = -1;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public int getLargestPoolSize() {
        return largestPoolSize;
    }

    public void setLargestPoolSize(int largestPoolSize) {
        this.largestPoolSize = largestPoolSize;
    }

    public ThreadPoolExecutor getTarget() {
        return target;
    }

    public void setTarget(ThreadPoolExecutor target) {
        this.target = target;
    }
}
