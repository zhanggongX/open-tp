package cn.opentp.server.domain.threadpool;

import cn.opentp.core.thread.pool.ThreadPoolState;

import java.util.List;

public interface ThreadPoolRepository {

    List<String> findByIpAndPid(String ipAndPid);

    ThreadPoolState info(String ipAndPid, String tpName);
}
