package mobi.myseries.application.features.backend;

public class RemoteStoreApiNotAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    public RemoteStoreApiNotAvailableException() {}

    public RemoteStoreApiNotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    public RemoteStoreApiNotAvailableException(Throwable throwable) {
        super(throwable);
    }

    public RemoteStoreApiNotAvailableException(String detailMessage,
            Throwable throwable) {
        super(detailMessage, throwable);
    }

}
