package br.edu.ufcg.aweseries.thetvdb;

public class NonExistentSeriesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NonExistentSeriesException() {
        super("There is no such series");
    }

    public NonExistentSeriesException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NonExistentSeriesException(String detailMessage) {
        super(detailMessage);
    }

    public NonExistentSeriesException(Throwable throwable) {
        super("There is no such series", throwable);
    }

}
