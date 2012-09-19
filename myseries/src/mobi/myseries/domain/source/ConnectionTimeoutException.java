package mobi.myseries.domain.source;

public class ConnectionTimeoutException extends Exception {
	private static final long serialVersionUID = 1L;

    public ConnectionTimeoutException() {
        super();
    }

    public ConnectionTimeoutException(Throwable cause) {
        super(cause);
    }
}

