package cn.opentp.core.util;

import java.util.concurrent.atomic.AtomicLong;

public class MessageTraceIdUtil {

    private static final AtomicLong atomicTraceId = new AtomicLong(1024);

    public static long traceId() {
        return atomicTraceId.getAndIncrement();
    }
}
