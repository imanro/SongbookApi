package songbook.domain.song.port.in;

public class SyncSongContentSongNotFoundException extends Exception {

    public SyncSongContentSongNotFoundException() {
        super();
    }

    public SyncSongContentSongNotFoundException(String message) {
        super(message);
    }

    public SyncSongContentSongNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
