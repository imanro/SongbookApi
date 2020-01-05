package songbook.importer.service;

public class ContentImporterException extends Exception {
     ContentImporterException() {
        super();
    }

     ContentImporterException(String message) {
        super(message);
    }

     ContentImporterException(String message, Throwable cause) {
        super(message, cause);
    }

     ContentImporterException(Throwable cause) {
        super(cause);
    }
}
