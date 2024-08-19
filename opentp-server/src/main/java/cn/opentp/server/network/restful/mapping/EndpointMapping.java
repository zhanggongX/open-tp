package cn.opentp.server.network.restful.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求映射类
 *
 * @author Leo
 * @date 2018/3/16
 */
public final class EndpointMapping {

    private String url;

    private String className;

    private String classMethod;

    private List<EndpointMappingParam> params = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassMethod() {
        return classMethod;
    }

    public void setClassMethod(String classMethod) {
        this.classMethod = classMethod;
    }

    public List<EndpointMappingParam> getParams() {
        return params;
    }

    public void setParams(List<EndpointMappingParam> params) {
        this.params = params;
    }
}
