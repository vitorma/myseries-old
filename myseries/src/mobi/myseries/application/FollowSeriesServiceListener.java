package mobi.myseries.application;

import mobi.myseries.domain.model.Series;

public interface FollowSeriesServiceListener {

    public void onSucess(Series series);

    public void onFaluire(Throwable exception);

    public void onStart();

    public void onFinish();

}
