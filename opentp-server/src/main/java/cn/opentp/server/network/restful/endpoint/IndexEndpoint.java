package cn.opentp.server.network.restful.endpoint;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.restful.annotation.GetMapping;
import cn.opentp.server.network.restful.annotation.RequestMapping;
import cn.opentp.server.network.restful.annotation.RestController;
import cn.opentp.server.network.restful.dto.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

@RestController
@RequestMapping("/")
public class IndexEndpoint {

    @GetMapping("/")
    public BaseRes<String> index() {
        return BaseRes.success("hello openTp");
    }
}
