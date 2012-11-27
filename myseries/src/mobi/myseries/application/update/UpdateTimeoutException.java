package mobi.myseries.application.update;

public class UpdateTimeoutException extends Exception {
    public UpdateTimeoutException(Exception e) {
        super(e);
    }
}
