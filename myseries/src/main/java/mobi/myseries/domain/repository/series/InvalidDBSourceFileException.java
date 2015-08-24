package mobi.myseries.domain.repository.series;

public class InvalidDBSourceFileException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidDBSourceFileException() {
        super();
    }

    public InvalidDBSourceFileException(String detailMessage,
            Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidDBSourceFileException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidDBSourceFileException(Throwable throwable) {
        super(throwable);
    }
}
