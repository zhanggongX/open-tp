package cn.opentp.server.configuration;

import cn.opentp.server.constant.OpentpServerConstant;

public class OpentpProperties {

    private int reportServerPort;
    private int transportServerPort;
    private int httpServerPort;

    /**
     * 配置，设置默认值
     */
    public OpentpProperties() {
        reportServerPort = OpentpServerConstant.DEFAULT_REPORT_SERVER_PORT;
        transportServerPort = OpentpServerConstant.DEFAULT_TRANSPORT_SERVER_PORT;
        httpServerPort = OpentpServerConstant.DEFAULT_REST_SERVER_PORT;
    }

    public int getReportServerPort() {
        return reportServerPort;
    }

    public void setReportServerPort(int reportServerPort) {
        this.reportServerPort = reportServerPort;
    }

    public int getTransportServerPort() {
        return transportServerPort;
    }

    public void setTransportServerPort(int transportServerPort) {
        this.transportServerPort = transportServerPort;
    }

    public int getHttpServerPort() {
        return httpServerPort;
    }

    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }
}
