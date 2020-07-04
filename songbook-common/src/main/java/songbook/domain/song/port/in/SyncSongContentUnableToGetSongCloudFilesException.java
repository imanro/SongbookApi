package songbook.domain.song.port.in;

public class SyncSongContentUnableToGetSongCloudFilesException extends Exception {

    public SyncSongContentUnableToGetSongCloudFilesException() {
        super();
    }

    public SyncSongContentUnableToGetSongCloudFilesException(String message) {
        super(message);
    }

    public SyncSongContentUnableToGetSongCloudFilesException(String message, Throwable cause) {
        super(message, cause);
    }
}
