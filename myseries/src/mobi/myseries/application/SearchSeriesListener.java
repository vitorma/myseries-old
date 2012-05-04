package mobi.myseries.application;

import java.util.List;

import mobi.myseries.domain.model.Series;

public interface SearchSeriesListener {

    public void onSucess(List<Series> series);

    public void onFaluire(Throwable exception);

    public void onProgress();

}
