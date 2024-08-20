package cn.opentp.server.network.restful.convert;

/**
 * 数据转换器接口
 * 
 * @author zg
 */
public interface Converter<T> {

    /**
     * 类型转换
     * 
     * @param source
     * @return
     */
    T convert(Object source);

}
