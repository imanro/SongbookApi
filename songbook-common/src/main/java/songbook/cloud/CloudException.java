package songbook.cloud;


public class CloudException extends Exception {
    public CloudException() {
        super();
    }

    public CloudException(String message) {
        super(message);
    }

    public CloudException(String message, Throwable e) {
        super(message, e);
    }
}
