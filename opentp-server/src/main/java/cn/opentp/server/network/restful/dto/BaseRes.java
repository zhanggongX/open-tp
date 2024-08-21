package cn.opentp.server.network.restful.dto;

/**
 * 统一返回结果
 *
 * @param <T>
 */
public class BaseRes<T> {

    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static BaseRes<Void> fail(String code, String message) {
        BaseRes<Void> res = new BaseRes<>();
        res.setCode(code);
        res.setMessage(message);
        return res;
    }

    public static <T> BaseRes<T> success(T data) {
        BaseRes<T> res = new BaseRes<>();
        res.setCode(BaseResCode.SUCCESS.getCode());
        res.setMessage(BaseResCode.SUCCESS.getMessage());
        res.setData(data);
        return res;
    }

    public static <T> BaseRes<T> success() {
        BaseRes<T> res = new BaseRes<>();
        res.setCode(BaseResCode.SUCCESS.getCode());
        res.setMessage(BaseResCode.SUCCESS.getMessage());
        return res;
    }
}
