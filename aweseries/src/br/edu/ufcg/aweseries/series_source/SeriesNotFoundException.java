package br.edu.ufcg.aweseries.series_source;

public class SeriesNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SeriesNotFoundException() {
        super();
    }

    public SeriesNotFoundException(Throwable cause) {
        super(cause);
    }
}
