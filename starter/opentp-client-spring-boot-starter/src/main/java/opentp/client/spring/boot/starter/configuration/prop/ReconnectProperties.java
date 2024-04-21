package opentp.client.spring.boot.starter.configuration.prop;

public class ReconnectProperties {

    private Long initialDelay;
    private Long period;

    public Long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }
}
