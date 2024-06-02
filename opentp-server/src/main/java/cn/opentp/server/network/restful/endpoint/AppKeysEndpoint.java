package cn.opentp.server.network.restful.endpoint;

import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.restful.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Collections;
import java.util.List;

public class AppKeysEndpoint extends AbstractEndpointAdapter<List<String>> {

    @Override
    public BaseRes<List<String>> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        // todo 获取用户信息
        // todo 获取用户的 appKeys
        // 当前直接返回唯一的 appKey
        List<String> appKeys = Collections.singletonList(OpentpServerConstant.ADMIN_DEFAULT_APP);
        return BaseRes.success(appKeys);
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
