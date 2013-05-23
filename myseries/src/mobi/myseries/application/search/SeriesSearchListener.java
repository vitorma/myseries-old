package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.domain.model.Series;

public interface SeriesSearchListener {
    public void onStart();
    public void onFinish();
    public void onSucess(List<Series> results);
    public void onFailure(Exception exception);
}
