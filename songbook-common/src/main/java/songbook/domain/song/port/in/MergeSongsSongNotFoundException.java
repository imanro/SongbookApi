package songbook.domain.song.port.in;

public class MergeSongsSongNotFoundException extends Exception {
    public MergeSongsSongNotFoundException() {
        super();
    }

    public MergeSongsSongNotFoundException(String message) {
        super(message);
    }

    public MergeSongsSongNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
