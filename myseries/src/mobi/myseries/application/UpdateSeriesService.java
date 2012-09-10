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
    private final List<UpdateListener> updateListeners;
    private boolean updateRunning = false;

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
        this.updateListeners = new LinkedList<UpdateListener>();

        this.registerSeriesUpdateListener(new UpdateListener() {

            @Override
            public void onUpdateSuccess() {
                updateRunning = false;
            }

            @Override
            public void onUpdateStart() {
                updateRunning = true;
            }

            @Override
            public void onUpdateFailure() {
                updateRunning = false;
            }
        });
    }

    public void update(Series series) {
        Validate.isNonNull(series, "series");

        List<Series> seriesToUpdate = new LinkedList<Series>();
        seriesToUpdate.add(series);

        this.seriesUpdater.update(seriesToUpdate);
    }

    public void updateSeriesData() {
        if (updateRunning) {
            Log.d("UpdateSeriesService", "Update already running");
            return;
        }

        this.seriesUpdater.update(seriesRepository.getAll());
    }

    public void registerSeriesUpdateListener(UpdateListener listener) {
        if (!this.updateListeners.contains(listener)) {
            this.updateListeners.add(listener);
        }
    }

    private enum UpdateResult {
        SUCCESS, CONNECTION_FAILED, UNKNOWN_ERROR
    };

    private class SeriesUpdater {
        private void update(Collection<Series> series) {
            Validate.isNonNull(series, "series");

            final List<Series> seriesToUpdate = new ArrayList<Series>(series);

            new AsyncTask<Void, Void, UpdateResult>() {
                @Override
                protected UpdateResult doInBackground(Void... params) {
                    for (final Series s : seriesToUpdate) {
                        Log.d(this.getClass().toString(), "Updating series: " + s.name());
                        Log.d(this.getClass().toString(), "Last updated: " + s.lastUpdate());
                        Series downloadedSeries;

                        try {
                            Log.d("SeriesUpdater", "Updating " + s.name());
                            downloadedSeries =
                                    seriesSource.fetchSeries(s.id(),
                                            localizationProvider.language());

                            downloadedSeries.mergeWith(s);
                            downloadedSeries.setLastUpdate(System.currentTimeMillis());
                            seriesRepository.update(s);

                            Log.d("SeriesUpdater", "Downloading poster of " + s.name());
                            imageProvider.downloadPosterOf(s);

                            Log.d("SeriesUpdater", s.name() + " updated.");

                        } catch (ParsingFailedException e) {
                            e.printStackTrace();
                            return UpdateResult.UNKNOWN_ERROR;

                        } catch (ConnectionFailedException e) {
                            e.printStackTrace();
                            return UpdateResult.CONNECTION_FAILED;

                        } catch (SeriesNotFoundException e) {
                            e.printStackTrace();
                            return UpdateResult.UNKNOWN_ERROR;

                        }
                    }

                    Log.d("SeriesUpdater", "Update complete.");

                    return UpdateResult.SUCCESS;
                }

                @Override
                protected void onPostExecute(UpdateResult result) {
                    if (UpdateResult.SUCCESS.equals(result)) {
                        for (UpdateListener listener : updateListeners) {
                            listener.onUpdateSuccess();
                        }
                    }

                    else {
                        for (UpdateListener listener : updateListeners) {
                            listener.onUpdateFailure();
                        }
                    }
                }

                @Override
                protected void onPreExecute() {
                    for (UpdateListener listener : updateListeners) {
                        listener.onUpdateStart();
                    }
                };

            }.execute();
        }
    }

    public void deregisterSeriesUpdateListener(UpdateListener listener) {
        this.updateListeners.remove(listener);
    }

    public void updateSeriesDataIfNeeded() {
        for (Series s : seriesRepository.getAll()) {
            if (System.currentTimeMillis() - s.lastUpdate() > this.automaticUpdateInterval()) {
                Log.d(this.getClass().toString(), s.name()
                        + " is outdated. Launching automatic update...");
                this.updateSeriesData();
                return;
            }
            
            Log.d(this.getClass().toString(), s.name() + " is up-to-date.");
            
        }        
        
        Log.d(this.getClass().toString(), "Update is not needed");
    }

    private Long automaticUpdateInterval() {
        return 24L * 60L * 60L * 1000L;
    }
}
