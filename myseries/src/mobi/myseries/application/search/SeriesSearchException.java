package mobi.myseries.application.search;

public class SeriesSearchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SeriesSearchException(Exception e) {
        super(e);
    }
}
