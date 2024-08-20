package cn.opentp.server.network.restful.convert;

/**
 * 整数转换器
 *
 * @author zg
 */
final class IntegerConverter implements Converter<Integer> {

    /**
     * 类型转换
     *
     * @param source
     * @return
     */
    @Override
    public Integer convert(Object source) {
        return Integer.parseInt(source.toString());
    }

}
