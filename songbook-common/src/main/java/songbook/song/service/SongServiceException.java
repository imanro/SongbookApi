package songbook.song.service;

public class SongServiceException extends Exception {
    public SongServiceException() {
        super();
    }

    public SongServiceException(String message) {
        super(message);
    }

    public SongServiceException(String message, Throwable e) {
        super(message, e);
    }
}
