package songbook.domain.song.port.in;

public class SyncSongContentNotInitializedException extends Exception {
    public SyncSongContentNotInitializedException(String message) {
        super(message);
    }
}
