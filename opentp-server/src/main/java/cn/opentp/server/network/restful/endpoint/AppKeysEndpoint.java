package cn.opentp.server.network.restful.endpoint;

import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.network.restful.annotation.GetMapping;
import cn.opentp.server.network.restful.annotation.RequestMapping;
import cn.opentp.server.network.restful.annotation.RestController;
import cn.opentp.server.network.restful.dto.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("appKeys")
public class AppKeysEndpoint {

    @GetMapping("")
    public BaseRes<List<String>> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        // todo 获取用户信息
        // todo 获取用户的 appKeys
        // 当前直接返回唯一的 appKey
        List<String> appKeys = Collections.singletonList(OpentpServerConstant.ADMIN_DEFAULT_APP);
        return BaseRes.success(appKeys);
    }
}
