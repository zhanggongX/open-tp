//package cn.opentp.gossip.core;
//
//
//import cn.opentp.gossip.enums.MessageTypeEnum;
//import com.alibaba.fastjson2.JSONObject;
//
///**
// * @author lvsq
// */
//public class GossipMessageFactory {
//    private static final GossipMessageFactory ourInstance = new GossipMessageFactory();
//    public static final String KEY_MSG_TYPE = "msgtype";
//    public static final String KEY_DATA = "data";
//    public static final String KEY_CLUSTER = "cluster";
//    public static final String KEY_FROM = "from";
//
//    public static GossipMessageFactory getInstance() {
//        return ourInstance;
//    }
//
//    private GossipMessageFactory() {
//    }
//
//    public JSONObject makeMessage(MessageTypeEnum type, String data, String cluster, String from) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(KEY_MSG_TYPE, type);
//        jsonObject.put(KEY_CLUSTER, cluster);
//        jsonObject.put(KEY_DATA, data);
//        jsonObject.put(KEY_FROM, from);
//        return jsonObject;
//    }
//}
