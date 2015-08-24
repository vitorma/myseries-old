package mobi.myseries.application.features.store.backend;

public class RemoteStoreApiException extends Exception {

    private static final long serialVersionUID = 1L;

    public RemoteStoreApiException() {}

    public RemoteStoreApiException(String detailMessage) {
        super(detailMessage);
    }

    public RemoteStoreApiException(Throwable throwable) {
        super(throwable);
    }

    public RemoteStoreApiException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
