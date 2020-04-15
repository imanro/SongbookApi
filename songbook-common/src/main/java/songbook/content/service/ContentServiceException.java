package songbook.content.service;

public class ContentServiceException extends Exception {
    public ContentServiceException() {
        super();
    }

    public ContentServiceException(String message) {
        super(message);
    }

    public ContentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
