package songbook.sharing.provider;

public class MailSenderException extends Exception {
    public MailSenderException() {
        super();
    }

    public MailSenderException(String message) {
        super(message);
    }

    public MailSenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
