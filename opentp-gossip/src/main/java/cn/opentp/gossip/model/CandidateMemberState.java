package cn.opentp.gossip.model;

import java.util.concurrent.atomic.AtomicInteger;


public class CandidateMemberState {
    private long heartbeatTime;
    private AtomicInteger downingCount;

    public CandidateMemberState(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
        this.downingCount = new AtomicInteger(0);
    }

    public void updateCount() {
        this.downingCount.incrementAndGet();
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public AtomicInteger getDowningCount() {
        return downingCount;
    }

    public void setDowningCount(AtomicInteger downingCount) {
        this.downingCount = downingCount;
    }

    @Override
    public String toString() {
        return "CandidateMemberState{" +
                "heartbeatTime=" + heartbeatTime +
                ", downingCount=" + downingCount.get() +
                '}';
    }
}
