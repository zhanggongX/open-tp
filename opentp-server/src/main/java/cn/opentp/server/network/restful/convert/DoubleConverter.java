package cn.opentp.server.network.restful.convert;

/**
 * 双精度转换器
 * 
 * @author zg
 */
final class DoubleConverter implements Converter<Double> {

    /**
     * 类型转换
     * 
     * @param source
     * @return
     */
    @Override
    public Double convert(Object source) {
        return Double.parseDouble(source.toString());
    }

}
