package opentp.client.spring.boot.starter.exception;

public class OpentpDupException extends RuntimeException {

    public OpentpDupException() {
        super();
    }

    public OpentpDupException(String ex) {
        super(ex);
    }
}
