package cn.opentp.server.network.restful.convert;

/**
 * 字符串转换器
 *
 * @author zg
 */
final class StringConverter implements Converter<String> {

    /**
     * 类型转换
     *
     * @param source
     * @return
     */
    @Override
    public String convert(Object source) {
        return source.toString();
    }

}
