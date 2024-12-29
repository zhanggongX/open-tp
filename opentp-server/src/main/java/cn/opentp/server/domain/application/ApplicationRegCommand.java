package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommand;

/**
 * application register command
 *
 * @author zg
 */
public class ApplicationRegCommand implements DomainCommand {

    /**
     * application name
     * english names are recommended
     */
    private String appName;
    /**
     * name
     * chinese names are recommended
     */
    private String name;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
