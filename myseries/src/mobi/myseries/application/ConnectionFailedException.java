package mobi.myseries.application;

public class ConnectionFailedException extends Exception {
    private static final long serialVersionUID = 1L;

    public ConnectionFailedException() {
        super();
    }

    public ConnectionFailedException(Throwable cause) {
        super(cause);
    }
}
