package cn.opentp.gossip.node.jackson;

import cn.opentp.gossip.node.GossipNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.StringWriter;

public class GossipNodeKeySerializer extends JsonSerializer<GossipNode> {

    @Override
    public void serialize(GossipNode gossipNode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer, gossipNode);
        jsonGenerator.writeFieldName(writer.toString());
    }
}
