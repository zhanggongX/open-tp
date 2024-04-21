package cn.opentp.core.auth;

import java.io.Serializable;

public class OpentpLicense implements Serializable {

    private String licenseKey;

    public OpentpLicense(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public OpentpLicense() {
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
}
