package br.edu.ufcg.aweseries.series_source;

public class ConnectionFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConnectionFailedException() {
        super();
    }

    public ConnectionFailedException(Throwable cause) {
        super(cause);
    }
}
