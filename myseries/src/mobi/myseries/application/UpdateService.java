package mobi.myseries.application;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import mobi.myseries.shared.Android;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateService {
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private LocalizationProvider localizationProvider;
    private ImageService imageProvider;
    private SeriesUpdater seriesUpdater;
    private final List<UpdateListener> updateListeners;
    private boolean updateRunning = false;
    private ErrorService errorService;

    public UpdateService(SeriesSource seriesSource, SeriesRepository seriesRepository,
            LocalizationProvider localizationProvider, ImageService imageProvider,
            ErrorService errorService) {

        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageProvider, "imageProvider");
        Validate.isNonNull(errorService, "errorService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageProvider = imageProvider;
        this.errorService = errorService;
        seriesUpdater = new SeriesUpdater();
        updateListeners = new LinkedList<UpdateListener>();

        registerSeriesUpdateListener(new UpdateListener() {

            @Override
            public void onUpdateSuccess() {
                updateRunning = false;
            }

            @Override
            public void onUpdateStart() {
                updateRunning = true;
            }

            @Override
            public void onUpdateNotNecessary() {
                // I don't care.
            }

            @Override
            public void onUpdateFailure(Exception e) {
                	updateRunning = false;

			}
        });
    }

    public void updateData() {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        seriesUpdater.update();
    }

    public void updateDataIfNeeded() {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        long earliestUpdateTime = earliestUpdatedDateOf(seriesRepository.getAll());

        if ((System.currentTimeMillis() - earliestUpdateTime) < automaticUpdateInterval()) {
            Log.d(getClass().getName(), "Update ran recently. Not running now.");

            for (UpdateListener listener : updateListeners) {
                listener.onUpdateNotNecessary();
            }
        } else {
            Log.d(getClass().getName(), "Launching update.");
            seriesUpdater.update();
        }
    }

    public void registerSeriesUpdateListener(UpdateListener listener) {
        if (!updateListeners.contains(listener)) {
            updateListeners.add(listener);
        }
    }

    public boolean isUpdating() {
        return updateRunning;
    }

    public class ComparatorByLastUpdate implements Comparator<Series> {
        @Override
        public int compare(Series series1, Series series2) {
            return (int) (series1.lastUpdate() - series2.lastUpdate());
        }
    }

    private static enum UpdateResult { NO_UPDATES_AVAILABLE, SUCCESS };
    private class SeriesUpdater {

        private void update() {
            final AsyncTask<Void, Void, AsyncTaskResult<UpdateResult>> updateTask =
                    new AsyncTask<Void, Void, AsyncTaskResult<UpdateResult>>() {

                @Override
                protected AsyncTaskResult<UpdateResult> doInBackground(Void... params) {
                    try {

                        boolean updateAvailable =
                                fetchUpdateMetadataSince(earliestUpdatedDateOf(followedSeries()));

                        List<Series> seriesToUpdate =
                                seriesWithObsoleteDataIn(followedSeries());
                        List<Series> imagesToUpdate =
                                seriesWithObsoletePosterIn(followedSeries());

                        if (!updateAvailable
                                || ((seriesToUpdate.size() == 0) && (imagesToUpdate.size() == 0))) {
                            return new AsyncTaskResult<UpdateService.UpdateResult>(
                                    UpdateResult.NO_UPDATES_AVAILABLE);
                        }

                        Collections.sort(seriesToUpdate, new ComparatorByLastUpdate());

                        for (final Series s : followedSeries()) {

                            if (seriesToUpdate.contains(s)) {
                                updateDataOf(s);
                                Log.d(getClass().getName(), "Data of " + s.name() + " updated.");
                            }


                            if (imagesToUpdate.contains(s)) {
                                updatePosterOf(s);
                                Log.d(getClass().getName(), "Poster of " + s.name()
                                        + " updated.");
                            }

                            s.setLastUpdate(System.currentTimeMillis());
                            seriesRepository.update(s);
                        }

                    } catch (ParsingFailedException e) {
                        e.printStackTrace();
                        return new AsyncTaskResult<UpdateResult>(e);

                    } catch (ConnectionFailedException e) {
                        e.printStackTrace();
                        return new AsyncTaskResult<UpdateResult>(e);

                    } catch (SeriesNotFoundException e) {
                        e.printStackTrace();
                        return new AsyncTaskResult<UpdateResult>(e);

                    } catch (UpdateMetadataUnavailableException e) {
                        e.printStackTrace();
                        return new AsyncTaskResult<UpdateResult>(e);

                    } catch (ConnectionTimeoutException e) {
                        e.printStackTrace();
                        return new AsyncTaskResult<UpdateResult>(e);
                    }

                    Log.d(getClass().getName(), "Update complete.");

                    return new AsyncTaskResult<UpdateService.UpdateResult>(UpdateResult.SUCCESS);
                }

                @Override
                protected void onPostExecute(AsyncTaskResult<UpdateResult> result) {
                    if (result.error() != null) {
                    	for (UpdateListener listener : updateListeners) {
                            listener.onUpdateFailure(result.error());
                        }
                    }
                    if (UpdateResult.SUCCESS.equals(result.result())) {
                        for (UpdateListener listener : updateListeners) {
                            listener.onUpdateSuccess();
                        }
                    } else if (UpdateResult.NO_UPDATES_AVAILABLE.equals(result.result())) {
                        for (UpdateListener listener : updateListeners) {
                            listener.onUpdateNotNecessary();
                        }
                    }
                }

                @Override
                protected void onPreExecute() {
                    for (UpdateListener listener : updateListeners) {
                        listener.onUpdateStart();
                    }
                }

                private void updateDataOf(Series series) throws ParsingFailedException,
                ConnectionFailedException, SeriesNotFoundException,
                ConnectionTimeoutException {

                    Log.d(getClass().getName(),
                            "Updating series: " + series.name());

                    Log.d(getClass().getName(),
                            "Last updated: " + series.lastUpdate());

                    Series downloadedSeries;

                    Log.d(getClass().getName(), "Updating " + series.name());

                    downloadedSeries = seriesSource.fetchSeries(series.id(),
                            localizationProvider.language());

                    series.mergeWith(downloadedSeries);
                }

                private void updatePosterOf(Series series) {
                    Log.d(getClass().getName(), "Downloading poster of " + series.name());
                    imageProvider.downloadPosterOf(series);
                }

                private boolean fetchUpdateMetadataSince(long dateInMiliseconds)
                        throws ConnectionFailedException, ConnectionTimeoutException,
                        ParsingFailedException, UpdateMetadataUnavailableException {
                    return seriesSource.fetchUpdateMetadataSince(dateInMiliseconds);
                }

                private List<Series> seriesWithObsoletePosterIn(
                        Collection<Series> seriesToFilter) {
                    List<Series> filtered = new LinkedList<Series>();

                    Map<Integer, String> availableUpdates =
                            seriesSource.posterUpdateMetadata();

                    for (Series series : seriesToFilter) {
                        if (availableUpdates.containsKey(series.id())) {
                            Log.d(getClass().getName(),
                                    "Update available for poster of " + series.name());

                            series.setPosterFilename(availableUpdates.get(series.id()));
                            seriesRepository.update(series);

                            filtered.add(series);
                        } else {
                            Log.d(getClass().getName(),
                                    "No updates found for poster of " + series.name());
                        }
                    }

                    return filtered;
                }

                private List<Series> seriesWithObsoleteDataIn(
                        Collection<Series> seriesToFilter) {

                    long earliestUpdateTime = earliestUpdatedDateOf(seriesToFilter);

                    if ((System.currentTimeMillis() - earliestUpdateTime) > oneMonth()) {
                        Log.d(getClass().getName(),
                                "Too long since last update, update all forced");

                        return new LinkedList<Series>(seriesToFilter);
                    }

                    List<Series> filtered = new LinkedList<Series>();

                    Collection<Integer> availableUpdates =
                            seriesSource.seriesUpdateMetadata();

                    for (Series series : seriesToFilter) {
                        if (availableUpdates.contains(series.id())) {
                            Log.d(getClass().getName(),
                                    "Update available for " + series.name() + "!");

                            filtered.add(series);
                        } else {
                            Log.d(getClass().getName(),
                                    "No updates found for " + series.name());
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
            };

            if (Android.isHoneycombOrHigher()) {
                updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                updateTask.execute();
            }
        }
    }

    public void deregisterSeriesUpdateListener(UpdateListener listener) {
        updateListeners.remove(listener);
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
