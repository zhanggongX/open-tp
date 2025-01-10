package cn.opentp.server.service;

import cn.opentp.core.thread.pool.ThreadPoolState;

import java.util.List;

public interface ThreadPoolService {

    List<String> findByIpAndPid(String ipAndPid);

    ThreadPoolState info(String ipAndPid, String tpName);
}
