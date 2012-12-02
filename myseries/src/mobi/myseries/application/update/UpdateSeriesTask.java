package mobi.myseries.application.update;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.SeriesSource;
import android.util.Log;

class UpdateSeriesTask implements UpdateTask {
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

        } catch (Exception e) {
            e.printStackTrace();
            this.result = new UpdateResult().withError(e);

        }

        this.result = new UpdateResult();
    }

    @Override
    public UpdateResult result() {
        return this.result;
    }
}
