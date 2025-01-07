package cn.opentp.server.domain.threadpool;

import java.util.List;

public interface ThreadPoolRepository {

    List<String> findByIpAndPid(String ipAndPid);
}
