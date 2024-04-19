package opentp.client.spring.boot.starter.exception;

public class ThreadPoolKeyDupException extends RuntimeException {

    public ThreadPoolKeyDupException() {
        super();
    }

    public ThreadPoolKeyDupException(String ex) {
        super(ex);
    }
}
