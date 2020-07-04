package songbook.domain.song.port.in;

public class DetachSongFromTagTagNotFoundException extends Exception {
    public DetachSongFromTagTagNotFoundException() {
        super();
    }

    public DetachSongFromTagTagNotFoundException(String message) {
        super(message);
    }

    public DetachSongFromTagTagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
