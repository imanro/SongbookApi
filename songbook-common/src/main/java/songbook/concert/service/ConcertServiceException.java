package songbook.concert.service;

public class ConcertServiceException extends Exception {

    public ConcertServiceException() {
        super();
    }

    public ConcertServiceException(String message) {
        super(message);
    }

    public ConcertServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
