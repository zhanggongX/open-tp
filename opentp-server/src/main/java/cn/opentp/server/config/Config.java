package cn.opentp.server.config;

import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.enums.DeployEnum;

public class Config {

    private int reportServerPort;
    private int transportServerPort;
    private int httpServerPort;
    private String master;
    private String deploy;

    /**
     * 配置，设置默认值
     */
    public Config() {
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

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public DeployEnum getDeploy() {
        return DeployEnum.parse(this.deploy);
    }

    public void setDeploy(String deploy) {
        this.deploy = deploy;
    }
}
