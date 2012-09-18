package mobi.myseries.application;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import mobi.myseries.shared.Android;
import mobi.myseries.shared.Validate;

public class UpdateService {
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private LocalizationProvider localizationProvider;
    private ImageProvider imageProvider;
    private SeriesUpdater seriesUpdater;
    private final List<UpdateListener> updateListeners;
    private boolean updateRunning = false;

    public UpdateService(SeriesSource seriesSource, SeriesRepository seriesRepository,
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

    public void updateDataIfNeeded() {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        long earliestUpdateTime = earliestUpdatedDateOf(seriesRepository.getAll());

        if (System.currentTimeMillis() - earliestUpdateTime < automaticUpdateInterval()) {
            Log.d(getClass().getName(), "Update ran recently. Not running now.");
        } else {
            Log.d(getClass().getName(), "Launching update.");
            this.seriesUpdater.update();
        }
    }

    public void registerSeriesUpdateListener(UpdateListener listener) {
        if (!this.updateListeners.contains(listener)) {
            this.updateListeners.add(listener);
        }
    }

    public boolean isUpdating() {
        return updateRunning;
    }

    private enum UpdateResult {
        SUCCESS, CONNECTION_FAILED, UPDATE_METADATA_UNAVAILABLE, UNKNOWN_ERROR
    };

    public class ComparatorByLastUpdate implements Comparator<Series> {
        @Override
        public int compare(Series series1, Series series2) {
            return (int) (series1.lastUpdate() - series2.lastUpdate());
        }
    }

    private class SeriesUpdater {
        private void update() {
            final AsyncTask<Void, Void, UpdateResult> updateTask =
                    new AsyncTask<Void, Void, UpdateResult>() {
                        @Override
                        protected UpdateResult doInBackground(Void... params) {

                            try {
                                List<Series> seriesToUpdate = filterUpToDateFrom(followedSeries());

                                Collections.sort(seriesToUpdate, new ComparatorByLastUpdate());

                                for (final Series s : seriesToUpdate) {
                                    Log.d(this.getClass().toString(),
                                            "Updating series: " + s.name());
                                    Log.d(this.getClass().toString(),
                                            "Last updated: " + s.lastUpdate());
                                    Series downloadedSeries;

                                    Log.d("SeriesUpdater", "Updating " + s.name());
                                    downloadedSeries =
                                            seriesSource.fetchSeries(s.id(),
                                                    localizationProvider.language());

                                    downloadedSeries.mergeWith(s);
                                    downloadedSeries.setLastUpdate(System.currentTimeMillis());
                                    seriesRepository.update(downloadedSeries);

                                    Log.d("SeriesUpdater", "Downloading poster of " + s.name());
                                    imageProvider.downloadPosterOf(s);

                                    Log.d("SeriesUpdater", s.name() + " updated.");
                                }

                            } catch (ParsingFailedException e) {
                                e.printStackTrace();
                                return UpdateResult.UNKNOWN_ERROR;

                            } catch (ConnectionFailedException e) {
                                e.printStackTrace();
                                return UpdateResult.CONNECTION_FAILED;

                            } catch (SeriesNotFoundException e) {
                                e.printStackTrace();
                                return UpdateResult.UNKNOWN_ERROR;
                            } catch (UpdateMetadataUnavailableException e) {
                                e.printStackTrace();
                                return UpdateResult.UPDATE_METADATA_UNAVAILABLE;
                            }

                            Log.d("SeriesUpdater", "Update complete.");

                            return UpdateResult.SUCCESS;
                        }

                        private List<Series> filterUpToDateFrom(Collection<Series> seriesToFilter)
                                throws ConnectionFailedException, ParsingFailedException,
                                UpdateMetadataUnavailableException {

                            long earliestUpdateTime = earliestUpdatedDateOf(seriesToFilter);

                            if (System.currentTimeMillis() - earliestUpdateTime > oneMonth()) {
                                Log.d(getClass().getName(),
                                        "Too long since last update, update all forced");

                                return new LinkedList<Series>(seriesToFilter);
                            }

                            List<Series> filtered = new LinkedList<Series>();

                            Collection<Integer> availableUpdates =
                                    seriesSource.fetchUpdatesSince(earliestUpdateTime);

                            for (Series series : seriesToFilter) {
                                if (availableUpdates.contains(series.id())) {
                                    Log.d(getClass().getName(),
                                            "Update available for " + series.name() + "!");

                                    filtered.add(series);
                                } else {
                                    Log.d(getClass().getName(),
                                            "No updates found for " + series.name()
                                                    + ". Refreshing last update time");

                                    series.setLastUpdate(System.currentTimeMillis());
                                    seriesRepository.update(series);
                                }
                            }

                            return filtered;
                        }

                        private long oneMonth() {
                            return 30L * 24L * 60L * 60L * 1000L;
                        }

                        private Collection<Series> followedSeries() {
                            return seriesRepository.getAll();
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
                    };

            if (Android.isHoneycombOrHigher()) {
                updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                updateTask.execute();
            }
        }
    }

    public void deregisterSeriesUpdateListener(UpdateListener listener) {
        this.updateListeners.remove(listener);
    }

    private static Long automaticUpdateInterval() {
        return 12L * 60L * 60L * 1000L;
    }

    private static long earliestUpdatedDateOf(Collection<Series> series) {
        long d = Long.MAX_VALUE;

        for (Series s : series) {
            if (s.lastUpdate() < d) {
                d = s.lastUpdate();
            }
        }

        return d;
    }

}
