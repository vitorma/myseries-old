package mobi.myseries.application.update.task;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.update.UpdateResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import android.util.Log;

public class UpdateSeriesTask implements UpdateTask {
    private final SeriesSource source;
    private final LocalizationProvider localizationProvider;
    private final SeriesRepository repository;
    private final Series series;
    private UpdateResult result;

    public UpdateSeriesTask(SeriesRepository repository, SeriesSource source,
            LocalizationProvider localizationProvider, Series series) {
        this.source = source;
        this.localizationProvider = localizationProvider;
        this.repository = repository;
        this.series = series;
    }

    @Override
    public void run() {
        Series downloadedSeries;
        try {
            Log.d(getClass().getName(), "Downloading data of " + series.name());
            downloadedSeries = source.fetchSeries(series.id(), localizationProvider.language());

            Log.d(getClass().getName(), "Merging " + series.name());
            series.mergeWith(downloadedSeries);

            Log.d(getClass().getName(), "Saving " + series.name());
            repository.update(series);

        } catch (SeriesNotFoundException e) {
            e.printStackTrace();
            this.result = new UpdateResult().withError(e.withSeriesName(series.name()));
            return;

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
