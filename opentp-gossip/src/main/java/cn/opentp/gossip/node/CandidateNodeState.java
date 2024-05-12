package cn.opentp.gossip.node;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 候选节点状态
 */
public class CandidateNodeState {

    // 心跳时间
    private long heartbeatTime;
    // 未上线次数
    private AtomicInteger downingCount;

    public CandidateNodeState(long heartbeatTime) {
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
