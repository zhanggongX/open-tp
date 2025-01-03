package cn.opentp.server.domain.threadpool;

import cn.opentp.core.thread.pool.ThreadPoolState;
import io.netty.channel.Channel;

import java.util.List;

public class ThreadPoolReportCommand {
    private Channel channel;
    private List<ThreadPoolState> threadPoolStates;

    public ThreadPoolReportCommand(List<ThreadPoolState> threadPoolStates, Channel channel) {
        this.threadPoolStates = threadPoolStates;
        this.channel = channel;
    }

    public ThreadPoolReportCommand() {
    }

    public List<ThreadPoolState> getThreadPoolStates() {
        return threadPoolStates;
    }

    public void setThreadPoolStates(List<ThreadPoolState> threadPoolStates) {
        this.threadPoolStates = threadPoolStates;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
