package cn.opentp.server.service;

import cn.opentp.server.domain.application.ApplicationImpl;

import java.util.List;

/**
 * 应用业务代码
 *
 * @author zg
 */
public interface ApplicationService {

    List<ApplicationImpl> applications(String username);
}
