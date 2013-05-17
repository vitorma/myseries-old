package mobi.myseries.application.update;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import mobi.myseries.shared.CollectionFilter;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.os.Handler;
import android.util.Log;

// Java's type system does not allow us to declare Publisher more than once, even with different type arguments.
// Anyway, it de facto implements this interface.
public class UpdateService implements Publisher<UpdateListener>/*, Publisher<UpdateFinishListener>*/ {
    private final SeriesSource seriesSource;
    private final SeriesRepository seriesRepository;
    private final ImageService imageService;
    private final LocalizationProvider localizationProvider;
    private final BroadcastService broadcastService;

    private final ListenerSet<UpdateListener> updateListeners;
    private final ListenerSet<UpdateFinishListener> updateFinishListeners;

    private boolean updateRunning = false;

    private UpdateListener selfListener;
    private Handler handler;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public UpdateService(SeriesSource seriesSource, SeriesRepository seriesRepository,
            LocalizationProvider localizationProvider, ImageService imageService,
            BroadcastService broadcastService) {

        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageService, "imageService");
        Validate.isNonNull(broadcastService, "broadcastService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.imageService = imageService;
        this.localizationProvider = localizationProvider;
        this.broadcastService = broadcastService;

        this.updateListeners = new ListenerSet<UpdateListener>();
        this.updateFinishListeners = new ListenerSet<UpdateFinishListener>();

        this.selfListener = new UpdateListener() {

            @Override
            public void onUpdateSuccess() {
                Log.d(getClass().getName(), "Update finished successfully. :)");
                UpdateService.this.broadcastService.broadcastUpdate();
                UpdateService.this.updateRunning = false;
            }

            @Override
            public void onUpdateStart() {
                Log.d(getClass().getName(), "Update started.");
                UpdateService.this.updateRunning = true;
            }

            @Override
            public void onUpdateNotNecessary() {
                Log.d(getClass().getName(), "Update is not necessary.");
                UpdateService.this.updateRunning = false;
            }

            @Override
            public void onUpdateFailure(Exception e) {
                Log.d(getClass().getName(), "Update finished with failure. :(");
                UpdateService.this.broadcastService.broadcastUpdate();
                UpdateService.this.updateRunning = false;
            }
        };

        Validate.isTrue(register(selfListener),
                "UpdateService could not be registered as listener to update ");
    }

    public UpdateService withHandler(Handler handler) {
        this.handler = handler;

        return this;
    }

    public boolean isUpdating() {
        return updateRunning;
    }

    public void updateData() {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        update(true);
    }

    public void updateDataIfNeeded() {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }
        if (!UpdatePolicy.shouldUpdateNow()) {
            Log.d(getClass().getName(), "Update will not run.");
            return;
        }

        long lastSuccessfulUpdate = earliestUpdatedDateOf(followedSeries());

        if (timeSince(lastSuccessfulUpdate) < UpdatePolicy.automaticUpdateInterval()) {
            Log.d(getClass().getName(), "Update ran recently. Not running now.");
        } else {
            update(false);
            Log.d(getClass().getName(), "Launching update.");
        }
    }

    // interface Publisher<UpdateListener>

    @Override
    public boolean register(UpdateListener listener) {
        return updateListeners.register(listener);
    }

    @Override
    public boolean deregister(UpdateListener listener) {
        return updateListeners.deregister(listener);
    }

    // interface Publisher<UpdateFinishListener>

    public boolean register(UpdateFinishListener listener) {
        return updateFinishListeners.register(listener);
    }

    public boolean deregister(UpdateFinishListener listener) {
        return updateFinishListeners.deregister(listener);
    }

    private void update(boolean forceUpdateRecent) {
        notifyListenersOfUpdateStart();

        if (!UpdatePolicy.networkAvailable()) {
            notifyListenersOfUpdateFailure(new NetworkUnavailableException());
            return;
        }

        boolean updateAvailable = false;

        Collection<Series> seriesWithDataToUpdate = followedSeries();
        Collection<Series> seriesWithPosterToUpdate = followedSeries();

        long lastSuccessfulUpdate = earliestUpdatedDateOf(followedSeries());

        if (timeSince(lastSuccessfulUpdate) < UpdatePolicy.downloadEverythingInterval()) {

            try {
                updateAvailable = fetchUpdateMetadataSince(lastSuccessfulUpdate);
                Log.d(getClass().getName(), "Update Metadata Available? " + updateAvailable);

            } catch (Exception e) {
                e.printStackTrace();
                notifyListenersOfUpdateFailure(e);
                return;
            }

            CollectionFilter<Series> withOutdatedData =
                    new CollectionFilter<Series>(new SeriesIdInCollectionSpecification(
                            seriesSource.seriesUpdateMetadata()));

            CollectionFilter<Series> withOutdatedPoster =
                    new CollectionFilter<Series>(new SeriesIdInCollectionSpecification(
                            seriesSource.posterUpdateMetadata().keySet()));

            seriesWithDataToUpdate = withOutdatedData.in(followedSeries());
            seriesWithPosterToUpdate = withOutdatedPoster.in(followedSeries());

            if (!forceUpdateRecent) {
                CollectionFilter<Series> notRecentlyUpdated =
                        new CollectionFilter<Series>(new RecentlyUpdatedSpecification());

                seriesWithDataToUpdate = notRecentlyUpdated.in(seriesWithDataToUpdate);
                seriesWithPosterToUpdate = notRecentlyUpdated.in(seriesWithPosterToUpdate);
            }

            if (!updateAvailable
                    || ((seriesWithDataToUpdate.isEmpty()) && (seriesWithPosterToUpdate.isEmpty()))) {
                notifyListenersOfUpdateNotNecessary();
            }
        }

        try {
            for (final Series s : followedSeries()) {

                if (seriesWithDataToUpdate.contains(s)) {
                    /* UPDATE SERIES DATA */

                    UpdateTask updateSeriesTask;
                    UpdateResult result = null;

                    updateSeriesTask =
                            new UpdateSeriesTask(seriesRepository, seriesSource,
                                    localizationProvider, s);
                    Future<?> future = executor.submit(updateSeriesTask);
                    future.get(UpdatePolicy.updateTimeout(), UpdatePolicy.updateTimeoutUnit());
                    result = updateSeriesTask.result();

                    if (!result.success()) {
                        notifyListenersOfUpdateFailure(result.error());
                        return;
                    }
                } else {
                    Log.d(getClass().getName(), "Not updating data of " + s.name());
                }

                if (posterAvailableButNotDownloaded(s) || seriesWithPosterToUpdate.contains(s)) {
                    /* UPDATE POSTER */

                    UpdateTask updateSeriesTask;
                    UpdateResult result = null;

                    updateSeriesTask = new UpdatePosterTask(imageService, s);
                    Future<?> future = executor.submit(updateSeriesTask);
                    future.get(UpdatePolicy.updateTimeout(), UpdatePolicy.updateTimeoutUnit());
                    result = updateSeriesTask.result();

                    if (!result.success()) {
                        notifyListenersOfUpdateFailure(result.error());
                        return;
                    }
                } else {
                    Log.d(getClass().getName(), "Not updating poster of " + s.name());
                }

                s.setLastUpdate(System.currentTimeMillis());
                seriesRepository.update(s);
            }

        } catch (InterruptedException e) {
            // Should never happen
            e.printStackTrace();
            return;

        } catch (ExecutionException e) {
            e.printStackTrace();
            notifyListenersOfUpdateFailure((Exception) e.getCause());
            return;

        } catch (TimeoutException e) {
            e.printStackTrace();
            notifyListenersOfUpdateFailure(new UpdateTimeoutException(e));
            return;

        }

        notifyListenersOfUpdateSuccess();
    }

    private boolean posterAvailableButNotDownloaded(Series series) {
        return series.hasPoster() && (imageService.getPosterOf(series) == null);
    }

    private static long timeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }

    private Collection<Series> followedSeries() {
        return seriesRepository.getAll();
    }

    // Auxiliary

    private static long earliestUpdatedDateOf(Collection<Series> series) {
        long d = Long.MAX_VALUE;

        for (Series s : series) {
            if (s.lastUpdate() < d) {
                d = s.lastUpdate();
            }
        }

        return d;
    }

    private void notifyListenersOfUpdateStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateStart();
                }
            }
        });
    }

    private void notifyListenersOfUpdateNotNecessary() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateNotNecessary();
                }
            }
        });
    }

    private void notifyListenersOfUpdateSuccess() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateSuccess();
                }
                for (final UpdateFinishListener listener : updateFinishListeners) {
                    listener.onUpdateFinish();
                }
            }
        });
    }

    private void notifyListenersOfUpdateFailure(final Exception cause) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateFailure(cause);
                }
                for (final UpdateFinishListener listener : updateFinishListeners) {
                    listener.onUpdateFinish();
                }
            }
        });
    }

    boolean fetchUpdateMetadataSince(long dateInMiliseconds)
            throws ConnectionFailedException, ConnectionTimeoutException,
            ParsingFailedException, UpdateMetadataUnavailableException {
        return seriesSource.fetchUpdateMetadataSince(dateInMiliseconds);
    }
}
