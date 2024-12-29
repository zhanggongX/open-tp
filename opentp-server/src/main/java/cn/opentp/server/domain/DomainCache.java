package cn.opentp.server.domain;

import cn.opentp.server.domain.application.Application;

import java.util.concurrent.ConcurrentHashMap;

public class DomainCache {

    public static ConcurrentHashMap<String, Application> applicationMap = new ConcurrentHashMap<>();
}
