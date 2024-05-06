package cn.opentp.server;

import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.enums.DeployEnum;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class OpentpProperties {

    private int reportServerPort;
    private int transportServerPort;
    private int httpServerPort;
    private String deploy;
    private String cluster;

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

    public DeployEnum getDeploy() {
        return DeployEnum.parse(this.deploy);
    }

    public void setDeploy(String deploy) {
        this.deploy = deploy;
    }

    public List<SocketAddress> getCluster() {
        try {
            List<SocketAddress> socketAddresses = new ArrayList<>();

            String[] addresses = cluster.split(",");
            for (String address : addresses) {
                String[] addressInfo = address.split(":");
                socketAddresses.add(new InetSocketAddress(addressInfo[0], Integer.parseInt(addressInfo[1])));
            }

            return socketAddresses;
        } catch (Exception e) {
            throw new IllegalArgumentException("集群地址配置异常，请配置详情的IP:PORT");
        }
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
