package cn.opentp.core.jackson;

import cn.opentp.core.auth.ServerInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.StringWriter;

public class ServerInfoKeySerializer extends JsonSerializer<ServerInfo> {

    @Override
    public void serialize(ServerInfo serverInfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer, serverInfo);
        jsonGenerator.writeFieldName(writer.toString());
    }
}
