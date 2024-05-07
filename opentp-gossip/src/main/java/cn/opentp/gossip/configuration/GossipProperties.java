package cn.opentp.gossip.configuration;

/**
 * 属性配置
 */
public class GossipProperties {
    // 集群名
    private String clusterName = "opentp";
    // 定罪阈值
    private Integer phiConvictThreshold = 8;

    //发送消息超时时间。
    //1.连接建立失败时，超过此时间不再重试建立连接。
    //2.消息队列中超过这个时间不再发送。
    //3.故障检测时，同一个节点发来的消息，若时间间隔超过这个值，为了让这个节点尽快失效，不计入故障检测队列，故障检测队列只更新最后达到时间。
    //参考cassandra配置默认值
    private Long rpcTimeoutInMs = 10000L;

    //环的稳定时间，超过这个时间认为环已经达到稳定状态。
    //用于节点删除、维护justRemovedEndpoints节点的超时时间、以及endpointStateMap真正移除，触发onRemove事件的时间。
    private Integer ringDelayInMs = 30 * 1000;
    // 当前服务监听的地址
    private String listenAddress;  //"localhost:9001";
    // 集群其他服务地址
    private String seeds;   //"localhost:9001";

    public GossipProperties() {
    }

    public GossipProperties(String listenAddress, String seeds) {
        this.listenAddress = listenAddress;
        this.seeds = seeds;
    }

    public GossipProperties(String clusterName, Integer phiConvictThreshold,
                            Long rpcTimeoutInMs, Integer ringDelayInMs,
                            String listenAddress, String seeds) {
        this.clusterName = clusterName;
        this.phiConvictThreshold = phiConvictThreshold;
        this.rpcTimeoutInMs = rpcTimeoutInMs;
        this.ringDelayInMs = ringDelayInMs;
        this.listenAddress = listenAddress;
        this.seeds = seeds;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Integer getPhiConvictThreshold() {
        return phiConvictThreshold;
    }

    public void setPhiConvictThreshold(Integer phiConvictThreshold) {
        this.phiConvictThreshold = phiConvictThreshold;
    }

    public Long getRpcTimeoutInMs() {
        return rpcTimeoutInMs;
    }

    public void setRpcTimeoutInMs(Long rpcTimeoutInMs) {
        this.rpcTimeoutInMs = rpcTimeoutInMs;
    }

    public Integer getRingDelayInMs() {
        return ringDelayInMs;
    }

    public void setRingDelayInMs(Integer ringDelayInMs) {
        this.ringDelayInMs = ringDelayInMs;
    }

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public String getSeeds() {
        return seeds;
    }

    public void setSeeds(String seeds) {
        this.seeds = seeds;
    }
}

