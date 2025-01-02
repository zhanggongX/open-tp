package cn.opentp.server.infrastructure;

import cn.opentp.server.infrastructure.auth.DefaultLicenseFactory;
import cn.opentp.server.infrastructure.auth.LicenseFactory;
import com.google.inject.AbstractModule;

public class GuiceInfraModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LicenseFactory.class).to(DefaultLicenseFactory.class);
    }
}
