package songbook.content.service;

public class PdfProcessorException extends Exception {
    public PdfProcessorException() {
        super();
    }

    public PdfProcessorException(String message) {
        super(message);
    }

    public PdfProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
