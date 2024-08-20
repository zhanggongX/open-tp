package cn.opentp.server.network.restful.convert;

/**
 * 单精度转换器
 *
 * @author zg
 */
final class FloatConverter implements Converter<Float> {

    /**
     * 类型转换
     *
     * @param source
     * @return
     */
    @Override
    public Float convert(Object source) {
        return Float.parseFloat(source.toString());
    }

}
