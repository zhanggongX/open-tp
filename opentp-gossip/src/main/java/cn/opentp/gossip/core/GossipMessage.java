package cn.opentp.gossip.core;

import cn.opentp.gossip.enums.MessageTypeEnum;

public class GossipMessage {

    // MessageTypeEnum
    public String type;
    public String data;
    public String cluster;
    public String form;

    public GossipMessage() {
    }

    public GossipMessage(String type, String data, String cluster, String form) {
        this.type = type;
        this.data = data;
        this.cluster = cluster;
        this.form = form;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }
}
