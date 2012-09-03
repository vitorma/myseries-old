package mobi.myseries.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.Validate;

public class UpdateSeriesService {
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private LocalizationProvider localizationProvider;
    private ImageProvider imageProvider;
    private SeriesUpdater seriesUpdater;

    public UpdateSeriesService(SeriesSource seriesSource, SeriesRepository seriesRepository,
            LocalizationProvider localizationProvider, ImageProvider imageProvider) {

        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageProvider, "imageProvider");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageProvider = imageProvider;
        this.seriesUpdater = new SeriesUpdater();
    }

    public void update(Series series) {
        Validate.isNonNull(series, "series");

        List<Series> seriesToUpdate = new LinkedList<Series>();
        seriesToUpdate.add(series);

        this.seriesUpdater.update(seriesToUpdate);
    }

    public void updateSeriesData() {
        this.seriesUpdater.update(seriesRepository.getAll());
    }

    public void registerSeriesUpdateListener(Object obj) {
        // TODO
    }

    private enum UpdateResult {
        SUCCESS, CONNECTION_FAILED, UNKNOWN_ERROR
    };

    private class SeriesUpdater {
        private UpdateResult result = UpdateResult.SUCCESS;

        private void update(Collection<Series> series) {
            Validate.isNonNull(series, "series");

            final List<Series> seriesToUpdate = new ArrayList<Series>(series);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    for (final Series s : seriesToUpdate) {
                        Series downloadedSeries;

                        try {
                            Log.d("SeriesUpdater", "Updating " + s.name());
                            downloadedSeries =
                                    seriesSource.fetchSeries(s.id(),
                                            localizationProvider.language());
                            downloadedSeries.mergeWith(s);
                            seriesRepository.update(s);
                            
                            Log.d("SeriesUpdater", "Downloading poster of " + s.name());
                            imageProvider.downloadPosterOf(s);

                            Log.d("SeriesUpdater", s.name() + " updated.");
                        } catch (ParsingFailedException e) {
                            result = UpdateResult.UNKNOWN_ERROR;
                            // TODO: Anything to do?
                            e.printStackTrace();
                        } catch (ConnectionFailedException e) {
                            result = UpdateResult.CONNECTION_FAILED;
                            e.printStackTrace();
                            return null;
                        } catch (SeriesNotFoundException e) {
                            result = UpdateResult.UNKNOWN_ERROR;
                            e.printStackTrace();
                            // TODO: Anything to do?
                        }
                    }

                    return null;
                }

                protected void onPostExecute(Void result) {
                    Log.d("SeriesUpdater", "Update complete.");
                };

            }.execute();
        }
    }
}
