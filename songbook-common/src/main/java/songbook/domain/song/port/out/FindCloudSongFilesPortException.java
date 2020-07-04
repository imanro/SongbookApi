package songbook.domain.song.port.out;

public class FindCloudSongFilesPortException extends Exception {
    public FindCloudSongFilesPortException() {
        super();
    }

    public FindCloudSongFilesPortException(String message) {
        super(message);
    }

    public FindCloudSongFilesPortException(String message, Throwable cause) {
        super(message, cause);
    }
}
