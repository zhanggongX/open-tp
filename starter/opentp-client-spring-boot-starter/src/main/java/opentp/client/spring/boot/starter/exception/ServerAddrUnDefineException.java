package opentp.client.spring.boot.starter.exception;

public class ServerAddrUnDefineException extends RuntimeException {

    public ServerAddrUnDefineException() {
        super();
    }

    public ServerAddrUnDefineException(String ex) {
        super(ex);
    }

}
