package cn.opentp.server.repository;

import cn.opentp.server.domain.application.ApplicationRepository;
import cn.opentp.server.domain.connect.ConnectRepository;
import cn.opentp.server.domain.manager.ManagerRepository;
import cn.opentp.server.repository.rocksdb.OpentpRocksDB;
import cn.opentp.server.repository.rocksdb.OpentpRocksDBImpl;
import com.google.inject.AbstractModule;

public class GuiceRepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ManagerRepository.class).to(ManagerRepositoryImpl.class);
        bind(ApplicationRepository.class).to(ApplicationRepositoryImpl.class);
        bind(ConnectRepository.class).to(ConnectRepositoryImpl.class);

        bind(OpentpRocksDB.class).to(OpentpRocksDBImpl.class);
    }
}
