package mobi.myseries.application.backup.exception;

public class ExternalStorageNotAvailableException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExternalStorageNotAvailableException(Exception e) {
        super(e);
    }

    public ExternalStorageNotAvailableException() {
    }

}


