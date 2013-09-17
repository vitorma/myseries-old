package mobi.myseries.application.update;

import java.util.Map;

import mobi.myseries.domain.model.Series;

public class BaseUpdateListener implements UpdateListener {

    @Override
    public void onCheckingForUpdates() { }

    @Override
    public void onUpdateNotNecessary() { }

    @Override
    public void onUpdateProgress(int current, int total, Series currentSeries) { }

    @Override
    public void onUpdateSuccess() { }

    @Override
    public void onUpdateFailure(Exception cause) { }

    @Override
    public void onUpdateSeriesFailure(Map<Series, Exception> causes) { }

    @Override
    public void onUpdateFinish() { }
}
