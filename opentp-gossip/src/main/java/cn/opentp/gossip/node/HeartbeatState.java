package cn.opentp.gossip.node;

import cn.opentp.gossip.util.VersionGenerator;

import java.util.Comparator;

/**
 * 心跳状态
 */
public class HeartbeatState implements Comparable<HeartbeatState> {

    private long version;
    private long heartbeatTime;

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public long getVersion() {
        return version;
    }

    public HeartbeatState() {
        this.heartbeatTime = System.currentTimeMillis();
    }

    public long updateVersion() {
        this.heartbeatTime = System.currentTimeMillis();
        this.version = VersionGenerator.nextVersion();
        return version;
    }

    @Override
    public String toString() {
        return "HeartbeatState{" + "heartbeatTime=" + heartbeatTime + ", version=" + version + '}';
    }

    @Override
    public int compareTo(HeartbeatState otherState) {
        int compare = Long.compare(this.heartbeatTime, otherState.getHeartbeatTime());
        return compare == 0 ? Long.compare(this.version, otherState.getVersion()) : compare;
    }
}
