package mobi.myseries.application.update.exception;

public class NetworkUnavailableException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final String message = "Network connection is not available.";

    public NetworkUnavailableException() {
        super(message);
    }

}
