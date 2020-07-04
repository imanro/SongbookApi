package songbook.domain.song.port.in;

public class AttachSongToTagTagNotFoundException extends Exception {
    public AttachSongToTagTagNotFoundException() {
        super();
    }

    public AttachSongToTagTagNotFoundException(String message) {
        super(message);
    }

    public AttachSongToTagTagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
