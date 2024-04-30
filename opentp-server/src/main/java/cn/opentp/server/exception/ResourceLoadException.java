package cn.opentp.server.exception;

public class ResourceLoadException extends RuntimeException {

    private final static String DEFAULT_ERROR = "资源加载失败";

    public ResourceLoadException() {
        super(DEFAULT_ERROR);
    }

    public ResourceLoadException(Throwable cause) {
        super(DEFAULT_ERROR, cause);
    }

    public ResourceLoadException(String message) {
        super(message);
    }

    public ResourceLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
