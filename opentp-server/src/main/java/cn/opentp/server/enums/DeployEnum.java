package cn.opentp.server.enums;

public enum DeployEnum {

    cluster, standalone;

    public static DeployEnum parse(String deploy) {
        for (DeployEnum target : DeployEnum.values()) {
            if (target.name().equals(deploy)) {
                return target;
            }
        }
        // 默认单机部署
        return standalone;
    }
}
