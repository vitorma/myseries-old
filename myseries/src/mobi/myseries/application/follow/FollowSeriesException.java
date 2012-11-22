package mobi.myseries.application.follow;

import mobi.myseries.domain.model.Series;

public class FollowSeriesException extends Exception {

    private static final long serialVersionUID = 1L;
    private Series series;

    public FollowSeriesException(Exception e, Series series) {
        super(e);
        this.series = series;
    }
    
    public Series series(){
        return this.series;
    }
}
