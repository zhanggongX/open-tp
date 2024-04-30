package cn.opentp.core.auth;

import java.io.Serializable;

public class License implements Serializable {

    private String licenseKey;

    public License(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public License() {
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
}
