package cn.opentp.client.configuration;

public class ReconnectProperties {

    /**
     * 初始延时
     */
    private long initialDelay;
    /**
     * 周期
     */
    private long period;

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}
