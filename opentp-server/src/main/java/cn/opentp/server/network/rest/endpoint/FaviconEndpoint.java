package cn.opentp.server.network.rest.endpoint;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.rest.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class FaviconEndpoint extends AbstractEndpointAdapter<Void> {

    @Override
    public BaseRes<Void> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        return BaseRes.success();
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
