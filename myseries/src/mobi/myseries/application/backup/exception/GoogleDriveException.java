package mobi.myseries.application.backup.exception;

import java.io.IOException;

public class GoogleDriveException extends Exception {

    private static final long serialVersionUID = 1L;

    private Exception cause; 

    public GoogleDriveException(IOException e) {
        super(e);
        cause = e;
    }
    
    public Exception getCause() {
        return cause;
    }

}
