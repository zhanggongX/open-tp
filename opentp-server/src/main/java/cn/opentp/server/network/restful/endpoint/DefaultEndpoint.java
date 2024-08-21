package cn.opentp.server.network.restful.endpoint;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.restful.dto.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultEndpoint extends AbstractEndpointAdapter<String> {

    @Override
    public BaseRes<String> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        return BaseRes.success("opentp start success");
    }

    @Override
    public BaseRes<Void> doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        throw new EndpointUnSupportException();
    }

    @Override
    public BaseRes<Void> doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        throw new EndpointUnSupportException();
    }

    @Override
    public BaseRes<Void> doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        throw new EndpointUnSupportException();
    }
}
