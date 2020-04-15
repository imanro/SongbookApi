package songbook.sharing.service;

public class SharingServiceException extends Exception{
    public SharingServiceException() {
        super();
    }

    public SharingServiceException(String message) {
        super(message);
    }

    public SharingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
