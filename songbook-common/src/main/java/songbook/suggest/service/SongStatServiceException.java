package songbook.suggest.service;

public class SongStatServiceException extends Exception {
    public SongStatServiceException() {
        super();
    }

    public SongStatServiceException(String message) {
        super(message);
    }

    public SongStatServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
