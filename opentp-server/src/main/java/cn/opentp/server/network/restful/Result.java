package cn.opentp.server.network.restful;

public class Result<T> {

    private static final int SUCCESS_CODE = 200;
    private static final int FAIL_CODE = -1;
    private static final String SUCCESS_MESSAGE = "success";
    private static final String FAIL_MESSAGE = "操作失败";

    int code;

    String message;

    T data;

    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(SUCCESS_CODE, message, null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> fail() {
        return new Result<>(FAIL_CODE, FAIL_MESSAGE, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public boolean checkSuccess() {
        return code == 200;
    }

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

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
}
