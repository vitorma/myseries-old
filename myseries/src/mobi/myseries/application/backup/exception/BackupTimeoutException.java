package mobi.myseries.application.backup.exception;

import java.util.concurrent.TimeoutException;

public class BackupTimeoutException extends Exception {
    private static final long serialVersionUID = 1L;

    public BackupTimeoutException(TimeoutException e) {
        super(e);
    }
    
    public BackupTimeoutException() {
    }

}
