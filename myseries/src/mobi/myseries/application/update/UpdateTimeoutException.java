package mobi.myseries.application.update;

public class UpdateTimeoutException extends Exception {
    private static final long serialVersionUID = 1L;

    public UpdateTimeoutException(Exception e) {
        super(e);
    }

    public UpdateTimeoutException() {
    }

}
