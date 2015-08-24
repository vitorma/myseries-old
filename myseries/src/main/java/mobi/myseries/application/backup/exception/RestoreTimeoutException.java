package mobi.myseries.application.backup.exception;

import java.util.concurrent.TimeoutException;

public class RestoreTimeoutException extends Exception {
    private static final long serialVersionUID = 1L;

    public RestoreTimeoutException(TimeoutException e) {
        super(e);
    }

    public RestoreTimeoutException() {
    }

}
