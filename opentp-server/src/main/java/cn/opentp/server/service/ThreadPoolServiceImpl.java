package cn.opentp.server.service;

import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.domain.manager.ManagerRepository;
import cn.opentp.server.domain.threadpool.ThreadPoolRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class ThreadPoolServiceImpl implements ThreadPoolService {

    @Inject
    private ThreadPoolRepository threadPoolRepository;

    @Override
    public List<String> findByIpAndPid(String ipAndPid) {
        return threadPoolRepository.findByIpAndPid(ipAndPid);
    }

    @Override
    public ThreadPoolState info(String ipAndPid, String tpName) {
        return threadPoolRepository.info(ipAndPid, tpName);
    }
}
