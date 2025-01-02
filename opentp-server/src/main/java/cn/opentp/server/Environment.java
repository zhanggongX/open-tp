package cn.opentp.server;

import cn.opentp.core.auth.ServerInfo;
import cn.opentp.server.domain.manager.ManagerImpl;
import cn.opentp.server.infrastructure.constant.OpentpServerConstant;
import cn.opentp.server.infrastructure.enums.DeployEnum;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Environment {
    /**
     * 集群名
     */
    private String clusterName;
    /**
     * 当前节点名
     */
    private String nodeName;
    /**
     * 数据存储路径
     */
    private String pathData;
    /**
     * 日志存储路径
     */
    private String pathLogs;
    /**
     * tp 信息上报端口
     */
    private int receivePort;
    /**
     * 集群节点同步端口
     */
    private int transportPort;
    /**
     * Restful 服务端口
     */
    private int httpPort;
    /**
     * 配置的集群节点
     */
    private String clusterNodes;
    /**
     * 部署方式
     */
    private DeployEnum deploy;

    private ServerInfo serverInfo;

    private final ThreadLocal<ManagerImpl> managerHolder = new ThreadLocal<>();

    /**
     * 配置，设置默认值
     */
    public Environment() {
        this.clusterName = OpentpServerConstant.DEFAULT_CLUSTER;
//        this.nodeName
        this.pathData = OpentpServerConstant.DEFAULT_PATH_DATA;
        this.pathLogs = OpentpServerConstant.DEFAULT_PATH_LOGS;
        this.receivePort = OpentpServerConstant.DEFAULT_REPORT_SERVER_PORT;
        this.transportPort = OpentpServerConstant.DEFAULT_TRANSPORT_SERVER_PORT;
        this.httpPort = OpentpServerConstant.DEFAULT_REST_SERVER_PORT;
//        this.clusterNodes
    }


    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    public String getPathLogs() {
        return pathLogs;
    }

    public void setPathLogs(String pathLogs) {
        this.pathLogs = pathLogs;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public int getTransportPort() {
        return transportPort;
    }

    public void setTransportPort(int transportPort) {
        this.transportPort = transportPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public DeployEnum getDeploy() {
        return deploy;
    }

    public void setDeploy(DeployEnum deploy) {
        this.deploy = deploy;
    }

    public List<SocketAddress> parseClusterNodes() {
        if (this.clusterNodes == null || this.clusterNodes.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<SocketAddress> socketAddresses = new ArrayList<>();

            String[] addresses = clusterNodes.split(",");
            for (String address : addresses) {
                String[] addressInfo = address.split(":");
                socketAddresses.add(new InetSocketAddress(addressInfo[0], Integer.parseInt(addressInfo[1])));
            }

            return socketAddresses;
        } catch (Exception e) {
            throw new IllegalArgumentException("集群地址配置异常，请配置详情的IP:PORT");
        }
    }

    public void setServerInfo(ServerInfo selfInfo) {
        this.serverInfo = selfInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public ThreadLocal<ManagerImpl> getManagerHolder() {
        return managerHolder;
    }
}
