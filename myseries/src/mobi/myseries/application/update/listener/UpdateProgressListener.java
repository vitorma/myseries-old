package mobi.myseries.application.update.listener;

import java.util.Map;

import mobi.myseries.domain.model.Series;

public interface UpdateProgressListener {
    public void onCheckingForUpdates();
    public void onUpdateNotNecessary();

    public void onUpdateProgress(int current, int total, Series currentSeries);

    public void onUpdateSuccess();

    public void onUpdateFailure(Exception cause);
    public void onUpdateSeriesFailure(Map<Series, Exception> causes);
}
