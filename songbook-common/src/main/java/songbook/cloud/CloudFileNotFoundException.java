package songbook.cloud;

public class CloudFileNotFoundException extends CloudException {
    public CloudFileNotFoundException() {
        super();
    }

    public CloudFileNotFoundException(String message) {
        super(message);
    }

    public CloudFileNotFoundException(String message, Throwable e) {
        super(message, e);
    }
}
