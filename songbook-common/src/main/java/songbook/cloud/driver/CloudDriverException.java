package songbook.cloud.driver;

import songbook.cloud.CloudException;

public class CloudDriverException extends CloudException {
    public CloudDriverException() {
        super();
    }

    public CloudDriverException(String message) {
        super(message);
    }

    public CloudDriverException(String message, Throwable e) {
        super(message, e);
    }
}
