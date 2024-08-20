package cn.opentp.server.network.restful.convert;

/**
 * 长整数转换器
 *
 * @author zg
 */
final class LongConverter implements Converter<Long> {

    /**
     * 类型转换
     *
     * @param source
     * @return
     */
    @Override
    public Long convert(Object source) {
        return Long.parseLong(source.toString());
    }

}