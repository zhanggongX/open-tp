package cn.opentp.server.infrastructure.auth;

import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class DefaultLicenseFactory implements LicenseFactory {

    @Override
    public String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
