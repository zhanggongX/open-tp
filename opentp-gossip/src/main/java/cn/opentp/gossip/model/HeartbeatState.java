package cn.opentp.gossip.model;

import cn.opentp.gossip.util.VersionGenerator;

public class HeartbeatState {

    private long heartbeatTime;
    private long version;

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
        return "HeartbeatState{" +
                "heartbeatTime=" + heartbeatTime +
                ", version=" + version +
                '}';
    }
}
