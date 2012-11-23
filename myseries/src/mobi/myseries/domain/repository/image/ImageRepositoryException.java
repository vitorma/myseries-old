package mobi.myseries.domain.repository.image;

public class ImageRepositoryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ImageRepositoryException() {
        super();
    }

    public ImageRepositoryException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ImageRepositoryException(String detailMessage) {
        super(detailMessage);
    }

    public ImageRepositoryException(Throwable throwable) {
        super(throwable);
    }
}
