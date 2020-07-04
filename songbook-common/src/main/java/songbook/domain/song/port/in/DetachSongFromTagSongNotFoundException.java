package songbook.domain.song.port.in;

public class DetachSongFromTagSongNotFoundException extends Exception {
    public DetachSongFromTagSongNotFoundException() {
        super();
    }

    public DetachSongFromTagSongNotFoundException(String message) {
        super(message);
    }

    public DetachSongFromTagSongNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
