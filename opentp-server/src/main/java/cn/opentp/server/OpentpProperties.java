package cn.opentp.server;

import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.enums.DeployEnum;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class OpentpProperties {

    private int reportPort;
    private int gossipPort;
    private int httpPort;
    private String deploy;
    private String cluster;

    /**
     * 配置，设置默认值
     */
    public OpentpProperties() {
        reportPort = OpentpServerConstant.DEFAULT_REPORT_SERVER_PORT;
        gossipPort = OpentpServerConstant.DEFAULT_TRANSPORT_SERVER_PORT;
        httpPort = OpentpServerConstant.DEFAULT_REST_SERVER_PORT;
    }

    public int getReportPort() {
        return reportPort;
    }

    public void setReportPort(int reportPort) {
        this.reportPort = reportPort;
    }

    public int getGossipPort() {
        return gossipPort;
    }

    public void setGossipPort(int gossipPort) {
        this.gossipPort = gossipPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public DeployEnum getDeploy() {
        return DeployEnum.parse(this.deploy);
    }

    public void setDeploy(String deploy) {
        this.deploy = deploy;
    }


    public String getCluster() {
        return cluster;
    }

    public List<SocketAddress> cluster() {
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
