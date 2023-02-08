package exceptions;

public class KvTaskClientException extends RuntimeException {
    public KvTaskClientException(final String message) {
        super(message);
    }
}
