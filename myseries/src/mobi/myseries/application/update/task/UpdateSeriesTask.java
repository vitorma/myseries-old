package mobi.myseries.application.update.task;

import mobi.myseries.application.Environment;
import mobi.myseries.application.update.UpdateResult;
import mobi.myseries.domain.model.Series;
import android.util.Log;

public class UpdateSeriesTask implements UpdateTask {
    private Environment environment;
    private final Series series;
    private UpdateResult result;

    public UpdateSeriesTask(Environment environment, Series series) {
        this.environment = environment;
        this.series = series;
    }

    @Override
    public void run() {
        Series downloadedSeries;
        try {
            Log.d(getClass().getName(), "Downloading data of " + series.name());
            downloadedSeries = environment.traktApi().fetchSeries(series.id());

            Log.d(getClass().getName(), "Merging " + series.name());
            series.mergeWith(downloadedSeries);

            Log.d(getClass().getName(), "Saving " + series.name());
            environment.seriesRepository().update(series);
        } catch (Exception e) {
            e.printStackTrace();
            this.result = new UpdateResult().withError(e);
            return;

        }

        this.result = new UpdateResult();
    }

    @Override
    public UpdateResult result() {
        return this.result;
    }
}
