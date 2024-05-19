package cn.opentp.server.network.rest;

/**
 * 统一返回结果
 *
 * @param <T>
 */
public class BaseRes<T> {

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public static BaseRes<Void> fail(int code, String message) {
        BaseRes<Void> res = new BaseRes<>();
        res.setCode(-1);
        res.setMessage(message);
        return res;
    }

    public static <T> BaseRes<T> success(T data) {
        BaseRes<T> res = new BaseRes<>();
        res.setCode(200);
        res.setMessage("success");
        res.setData(data);
        return res;
    }

    public static <T> BaseRes<T> success() {
        BaseRes<T> res = new BaseRes<>();
        res.setCode(200);
        res.setMessage("success");
        return res;
    }
}
