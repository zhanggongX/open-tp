package cn.opentp.server.infrastructure.util;

import java.util.List;
import java.util.stream.Collectors;

public class PageUtil {

    public static <T> List<T> page(List<T> totalList, int page, int size) {
        return totalList.stream().skip((long) (page - 1) * size).limit(size).collect(Collectors.toList());
    }
}
