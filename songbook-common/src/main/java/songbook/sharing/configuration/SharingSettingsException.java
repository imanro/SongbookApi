package songbook.sharing.configuration;

public class SharingSettingsException extends Exception {
    public SharingSettingsException() {
        super();
    }

    public SharingSettingsException(String message) {
        super(message);
    }

    public SharingSettingsException(String message, Throwable cause) {
        super(message, cause);
    }
}
