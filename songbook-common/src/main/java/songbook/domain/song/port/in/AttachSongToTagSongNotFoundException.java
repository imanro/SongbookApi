package songbook.domain.song.port.in;

public class AttachSongToTagSongNotFoundException extends Exception {
    public AttachSongToTagSongNotFoundException() {
        super();
    }

    public AttachSongToTagSongNotFoundException(String message) {
        super(message);
    }

    public AttachSongToTagSongNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
