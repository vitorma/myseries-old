package mobi.myseries.application.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.update.exception.NetworkUnavailableException;
import mobi.myseries.application.update.exception.UpdateException;
import mobi.myseries.application.update.exception.UpdateTimeoutException;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.application.update.listener.UpdateListener;
import mobi.myseries.application.update.specification.RecentlyUpdatedSpecification;
import mobi.myseries.application.update.specification.SeriesIdInCollectionSpecification;
import mobi.myseries.application.update.task.UpdatePosterTask;
import mobi.myseries.application.update.task.UpdateSeriesTask;
import mobi.myseries.application.update.task.UpdateTask;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.SeriesSource;
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

    private final AtomicBoolean isUpdating;

    private final ExecutorService executor;
    private Handler handler;

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

        this.isUpdating = new AtomicBoolean(false);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public UpdateService withHandler(Handler handler) {
        this.handler = handler;

        return this;
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

    // Update methods

    public boolean isUpdating() {
        return this.isUpdating.get();
    }

    public void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                actualUpdateData();
            }
        }).start();
    }

    public void updateDataIfNeeded() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                actualUpdateDataIfNeeded();
            }
        }).start();
    }

    private void actualUpdateData() {
        if (!this.isUpdating.compareAndSet(false, true)) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        update(true);
    }

    private void actualUpdateDataIfNeeded() {
        if (!this.isUpdating.compareAndSet(false, true)) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        if (!UpdatePolicy.shouldUpdateNow()) {
            Log.d(getClass().getName(), "Update will not run.");
            this.isUpdating.set(false);
            return;
        }

        long lastSuccessfulUpdate = earliestUpdatedDateOf(followedSeries());

        if (timeSince(lastSuccessfulUpdate) < UpdatePolicy.automaticUpdateInterval()) {
            Log.d(getClass().getName(), "Update ran recently. Not running now.");
            this.isUpdating.set(false);
        } else {
            Log.d(getClass().getName(), "Launching update.");
            update(false);
        }
    }

    private void update(boolean forceUpdateRecent) {
        new UpdateTask2(forceUpdateRecent).run();
    }

    // Auxiliary

    private static long timeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }

    private Collection<Series> followedSeries() {
        return Collections.unmodifiableCollection(seriesRepository.getAll());
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

    private void notifyListenersOfUpdateStart() {
        Log.d(getClass().getName(), "Update started.");

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
        Log.d(getClass().getName(), "Update is not necessary.");
        this.isUpdating.set(false);

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
        Log.d(getClass().getName(), "Update finished successfully. :)");
        this.isUpdating.set(false);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateSuccess();
                }
            }
        });

        notifyListenersOfUpdateFinish();
    }

    private void notifyListenersOfUpdateFailure(final Exception cause) {
        Log.d(getClass().getName(), "Update finished with failure. :(");
        this.isUpdating.set(false);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateFailure(cause);
                }
            }
        });

        notifyListenersOfUpdateFinish();
    }

    private void notifyListenersOfUpdateFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateFinishListener listener : updateFinishListeners) {
                    listener.onUpdateFinish();
                }
            }
        });

        this.broadcastService.broadcastUpdate();
    }

    private class UpdateTask2 implements Runnable {
        private final boolean shouldUpdateRecentlyUpdatedSeriesAsWell;
        private final Collection<Series> followedSeries;

        public UpdateTask2(boolean shouldUpdateRecentlyUpdatedSeriesAsWell) {
            this.shouldUpdateRecentlyUpdatedSeriesAsWell = shouldUpdateRecentlyUpdatedSeriesAsWell;

            this.followedSeries = followedSeries();
        }

        private class WhatHasToBeUpdated {
            public boolean shouldUpdateAllSeries;
            public boolean updateIsAvailable;
            public Collection<Series> seriesWithDataToUpdate;
            public Collection<Series> seriesWithPosterToUpdate;

            public boolean isUpdateNecessary() {
                return shouldUpdateAllSeries
                        || updateIsAvailable
                        || !seriesWithDataToUpdate.isEmpty()
                        || !seriesWithPosterToUpdate.isEmpty();
            }
        }

        @Override
        public void run() {
            // TODO Should we keep this?
            notifyListenersOfUpdateStart();

            if (!networkConnectionIsAvailable()) {
                notifyListenersOfUpdateFailure(new NetworkUnavailableException());
                return;
            }

            WhatHasToBeUpdated whatHasToBeUpdated;
            try {
                whatHasToBeUpdated = checkForUpdates();
            } catch (Exception e) {
                notifyListenersOfUpdateFailure(e);
                return;
            }
            if (!whatHasToBeUpdated.isUpdateNecessary()) {
                notifyListenersOfUpdateNotNecessary();
                return;
            }

            Collection<Exception> errors = updateAllSeriesIn(whatHasToBeUpdated);
            if (!errors.isEmpty()) {
                notifyListenersOfUpdateFailure(errors.iterator().next());
                return;
            }

            notifyListenersOfUpdateSuccess();
        }

        private boolean networkConnectionIsAvailable() {
            return UpdatePolicy.networkAvailable();
        }

        private WhatHasToBeUpdated checkForUpdates() {
            // TODO notifyListenersOfCheckingForUpdates();

            WhatHasToBeUpdated result = new WhatHasToBeUpdated();

            result.seriesWithDataToUpdate = followedSeries;
            result.seriesWithPosterToUpdate = followedSeries;

            long lastSuccessfulUpdate = earliestUpdatedDateOf(followedSeries);
            result.shouldUpdateAllSeries =
                    !(timeSince(lastSuccessfulUpdate) < UpdatePolicy.downloadEverythingInterval());

            if (!result.shouldUpdateAllSeries) {
                // FIXME(Gabriel): SeriesSource should return all the information regarding update
                // of series since someday at once in fetchUpdateMetadataSince
                try {
                    result.updateIsAvailable = seriesSource.fetchUpdateMetadataSince(lastSuccessfulUpdate);
                    Log.d(getClass().getName(), "Is update metadata available? " + result.updateIsAvailable);
                } catch (Throwable e) {
                    throw new UpdateException(e);
                }

                CollectionFilter<Series> withOutdatedData =
                        new CollectionFilter<Series>(new SeriesIdInCollectionSpecification(
                                seriesSource.seriesUpdateMetadata()));

                CollectionFilter<Series> withOutdatedPoster =
                        new CollectionFilter<Series>(new SeriesIdInCollectionSpecification(
                                seriesSource.posterUpdateMetadata().keySet()));

                result.seriesWithDataToUpdate = withOutdatedData.in(followedSeries);
                result.seriesWithPosterToUpdate = withOutdatedPoster.in(followedSeries);

                if (!shouldUpdateRecentlyUpdatedSeriesAsWell) {
                    CollectionFilter<Series> notRecentlyUpdated =
                            new CollectionFilter<Series>(new RecentlyUpdatedSpecification());

                    result.seriesWithDataToUpdate = notRecentlyUpdated.in(result.seriesWithDataToUpdate);
                    result.seriesWithPosterToUpdate = notRecentlyUpdated.in(result.seriesWithPosterToUpdate);
                }
            }

            return result;
        }

        private Collection<Exception> updateAllSeriesIn(WhatHasToBeUpdated whatHasToBeUpdated) {
            // TODO notifyProgress

            Collection<Exception> errors = new ArrayList<Exception>();

            for (final Series s : followedSeries) {
                try {
                    if (whatHasToBeUpdated.seriesWithDataToUpdate.contains(s)) {
                        Log.d(getClass().getName(), "Updating data of " + s.name());
                        UpdateResult result = updateDataOf(s);

                        if (!result.success()) {
                            errors.add(result.error());
                            continue;
                        }
                    } else {
                        Log.d(getClass().getName(), "Not updating data of " + s.name());
                    }

                    if (whatHasToBeUpdated.seriesWithPosterToUpdate.contains(s) || posterAvailableButNotDownloaded(s)) {
                        Log.d(getClass().getName(), "Updating poster of " + s.name());
                        UpdateResult result = updatePosterOf(s); 

                        if (!result.success()) {
                            errors.add(result.error());
                            continue;
                        }
                    } else {
                        Log.d(getClass().getName(), "Not updating poster of " + s.name());
                    }

                    s.setLastUpdate(System.currentTimeMillis());
                    seriesRepository.update(s);

                } catch (InterruptedException e) {
                    // Should never happen
                    e.printStackTrace();
                    errors.add(e);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    errors.add((Exception) e.getCause());

                } catch (TimeoutException e) {
                    e.printStackTrace();
                    errors.add(new UpdateTimeoutException(e));

                }
            }

            return errors;
        }

        private UpdateResult updateDataOf(Series s) throws InterruptedException, ExecutionException, TimeoutException {
            UpdateTask updateSeriesTask =
                    new UpdateSeriesTask(seriesRepository, seriesSource, localizationProvider, s);

            Future<?> future = executor.submit(updateSeriesTask);
            future.get(UpdatePolicy.updateTimeout(), UpdatePolicy.updateTimeoutUnit());

            return updateSeriesTask.result();
        }

        private UpdateResult updatePosterOf(Series s) throws InterruptedException, ExecutionException, TimeoutException {
            UpdateTask updatePosterTask = new UpdatePosterTask(imageService, s);

            Future<?> future = executor.submit(updatePosterTask);
            future.get(UpdatePolicy.updateTimeout(), UpdatePolicy.updateTimeoutUnit());

            return updatePosterTask.result();
        }

        private boolean posterAvailableButNotDownloaded(Series series) {
            return series.hasPoster() && (imageService.getPosterOf(series) == null);
        }

        // Notifications ----------------------------------------------------------

        /*
        // TODO @Deprecated
        private void notifyListenersOfUpdateStart() {
            // TODO
        }

        private void notifyListenersOfCheckingForUpdates() {
            // TODO
        }

        private void notifyListenersOfUpdateProgress() {
            // TODO
        }

        private void notifyListenersOfUpdateNotNecessary() {
            // TODO
        }

        private void notifyListenersOfUpdateSuccess() {
            // TODO
        }

        private void notifyListenersOfUpdateFailure(Exception e) {
            // TODO
        }

        private void notifyListenersOfUpdateFinish() {
            // TODO
        }
        */
    }
}
