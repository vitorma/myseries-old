package mobi.myseries.application;

import java.util.List;

import mobi.myseries.domain.model.Series;

public interface TrendingSeriesListener {
    public void onStartLoading();
    public void onFinishLoading(List<Series> result);
}