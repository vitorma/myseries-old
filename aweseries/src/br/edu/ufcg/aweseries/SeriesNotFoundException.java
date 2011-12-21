package br.edu.ufcg.aweseries;

/**
 * This exception is thrown when the SeriesSource cannot find any series with the given id.
 */
public class SeriesNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static String DETAIL_MESSAGE = "No series could be found with the given id";

    public SeriesNotFoundException() {
        super(DETAIL_MESSAGE);
    }

    public SeriesNotFoundException(Throwable throwable) {
        super(DETAIL_MESSAGE, throwable);
    }
}
