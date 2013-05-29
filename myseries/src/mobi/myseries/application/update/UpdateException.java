package mobi.myseries.application.update;

public class UpdateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UpdateException(Throwable e) {
        super(e);
    }
}
