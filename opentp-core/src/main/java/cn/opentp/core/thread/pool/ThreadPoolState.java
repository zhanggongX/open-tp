package cn.opentp.core.thread.pool;

import java.io.Serializable;

public class ThreadPoolState implements Serializable {

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
     * 线程名
     */
    private String threadPoolName;

    public void flushDefault() {
        this.coreSize = -1;
        this.maxSize = -1;
        this.poolSize = -1;
        this.activeCount = -1;
        this.completedCount = -1;
        this.queueSize = -1;
        this.queueLength = -1;
        this.largestPoolSize = -1;
    }

    public void flushDefault(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        flushDefault();
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

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    @Override
    public String toString() {
        return "ThreadPoolState{" +
                "coreSize=" + coreSize +
                ", maxSize=" + maxSize +
                ", poolSize=" + poolSize +
                ", activeCount=" + activeCount +
                ", completedCount=" + completedCount +
                ", queueSize=" + queueSize +
                ", queueLength=" + queueLength +
                ", largestPoolSize=" + largestPoolSize +
                ", threadPoolName='" + threadPoolName + '\'' +
                '}';
    }
}
