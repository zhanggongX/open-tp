package cn.opentp.gossip.node;

import cn.opentp.gossip.util.VersionGenerator;

/**
 * 心跳状态
 */
public class HeartbeatState {

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
}
