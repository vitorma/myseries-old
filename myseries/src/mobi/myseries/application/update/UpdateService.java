package mobi.myseries.application.update;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import mobi.myseries.application.update.exception.UpdateTimeoutException;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.application.update.listener.UpdateProgressListener;
import mobi.myseries.application.update.specification.SeriesIdInCollectionSpecification;
import mobi.myseries.application.update.task.FetchUpdateMetadataTask;
import mobi.myseries.application.update.task.UpdatePosterTask;
import mobi.myseries.application.update.task.UpdateSeriesTask;
import mobi.myseries.application.update.task.UpdateTask;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.CollectionFilter;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.os.Handler;
import android.util.Log;

// Java's type system does not allow us to declare Publisher more than once, even with different type arguments.
// Anyway, it de facto implements these interfaces.
public class UpdateService implements Publisher<UpdateFinishListener>/*, Publisher<UpdateProgressListener>*/ {
    private final SeriesSource seriesSource;
    private final SeriesRepository seriesRepository;
    private final ImageService imageService;
    private final LocalizationProvider localizationProvider;
    private final BroadcastService broadcastService;

    private final ListenerSet<UpdateFinishListener> updateFinishListeners;
    private final ListenerSet<UpdateProgressListener> updateProgressListeners;

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

        this.updateFinishListeners = new ListenerSet<UpdateFinishListener>();
        this.updateProgressListeners = new ListenerSet<UpdateProgressListener>();

        this.isUpdating = new AtomicBoolean(false);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public UpdateService withHandler(Handler handler) {
        this.handler = handler;

        return this;
    }

    // interface Publisher<UpdateFinishListener>

    public boolean register(UpdateFinishListener listener) {
        return updateFinishListeners.register(listener);
    }

    public boolean deregister(UpdateFinishListener listener) {
        return updateFinishListeners.deregister(listener);
    }

    // interface Publisher<UpdateProgressListener>

    public boolean register(UpdateProgressListener listener) {
        return updateProgressListeners.register(listener);
    }

    public boolean deregister(UpdateProgressListener listener) {
        return updateProgressListeners.deregister(listener);
    }

    // Update methods

    public Long latestSuccessfulUpdate() {
        Collection<Series> followedSeries = followedSeries();

        if (followedSeries.isEmpty()) {
            return null;
        } else {
            return earliestUpdatedDateOf(followedSeries);
        }
    }

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

        update();
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

        Collection<Series> followedSeries = followedSeries();
        boolean thereMayBeSomethingToUpdate = !followedSeries.isEmpty();
        boolean updateRanRecently = thereMayBeSomethingToUpdate &&
                timeSince(earliestUpdatedDateOf(followedSeries)) < UpdatePolicy.automaticUpdateInterval();
        boolean thereAreSeriesWhosePostersAreNotDownloaded = thereAreSeriesWhosePostersAreNotDownloaded();

        if (!thereMayBeSomethingToUpdate || (updateRanRecently && !thereAreSeriesWhosePostersAreNotDownloaded)) {
            Log.d(getClass().getName(), "Update ran recently. Not running now.");
            this.isUpdating.set(false);
            return;
        }

        if (thereAreSeriesWhosePostersAreNotDownloaded) {
            Log.d(getClass().getName(), "There are series whose posters are not downloaded.");
        }

        Log.d(getClass().getName(), "Launching update.");
        update();
    }

    private void update() {
        new UpdateTask2().run();
    }

    // Auxiliary

    private static long timeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }

    private Collection<Series> followedSeries() {
        return Collections.unmodifiableCollection(this.seriesRepository.getAll());
    }

    private boolean posterAvailableButNotDownloaded(Series series) {
        return series.hasPoster() && (this.imageService.getPosterOf(series) == null);
    }

    private boolean thereAreSeriesWhosePostersAreNotDownloaded() {
        for (Series s : this.followedSeries()) {
            if (posterAvailableButNotDownloaded(s)) {
                return true;
            }
        }
        return false;
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

    // Notifications

    private void notifyListenersOfCheckingForUpdates() {
        Log.d(getClass().getName(), "Checking for updates.");

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateProgressListener listener : updateProgressListeners) {
                    listener.onCheckingForUpdates();
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
                for (final UpdateProgressListener listener : updateProgressListeners) {
                    listener.onUpdateNotNecessary();
                }
            }
        });

        notifyListenersOfUpdateFinish();
    }

    private void notifyListenersOfUpdateProgress(final int current, final int total, final Series currentSeries) {
        Log.d(getClass().getName(), "Update progress: " + current + "/" + total);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateProgressListener listener : updateProgressListeners) {
                    listener.onUpdateProgress(current, total, currentSeries);
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
                for (final UpdateProgressListener listener : updateProgressListeners) {
                    listener.onUpdateSuccess();
                }
            }
        });

        notifyListenersOfUpdateFinish();
    }

    private void notifyListenersOfUpdateFailure(final Exception cause) {
        Log.d(getClass().getName(), "Update finished with failure: " + cause.getClass().getName());
        this.isUpdating.set(false);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateProgressListener listener : updateProgressListeners) {
                    listener.onUpdateFailure(cause);
                }
            }
        });

        notifyListenersOfUpdateFinish();
    }

    private void notifyListenersOfUpdateSeriesFailure(final Map<Series, Exception> causes) {
        Log.d(getClass().getName(), "Update finished with failure when updating series.");
        this.isUpdating.set(false);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateProgressListener listener : updateProgressListeners) {
                    listener.onUpdateSeriesFailure(Collections.unmodifiableMap(causes));
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

        private class WhatHasToBeUpdated {
            public Collection<Series> seriesWithDataToUpdate;
            public Collection<Series> seriesWithPosterToUpdate;

            public boolean isUpdateNecessary() {
                return !seriesWithDataToUpdate.isEmpty() || !seriesWithPosterToUpdate.isEmpty();
            }

            public Set<Series> seriesToBeUpdated() {
                Set<Series> seriesToBeUpdated = new HashSet<Series>();

                seriesToBeUpdated.addAll(seriesWithDataToUpdate);
                seriesToBeUpdated.addAll(seriesWithPosterToUpdate);

                return seriesToBeUpdated;
            }
        }

        @Override
        public void run() {
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

            Map<Series, Exception> errors = updateAllSeriesIn(whatHasToBeUpdated);
            if (!errors.isEmpty()) {
                notifyListenersOfUpdateSeriesFailure(errors);
                return;
            }

            notifyListenersOfUpdateSuccess();
        }

        private boolean networkConnectionIsAvailable() {
            return UpdatePolicy.networkAvailable();
        }

        private WhatHasToBeUpdated checkForUpdates() throws Exception {
            notifyListenersOfCheckingForUpdates();

            Collection<Series> followedSeries = followedSeries();

            long lastSuccessfulUpdate = earliestUpdatedDateOf(followedSeries);
            boolean shouldUpdateAllSeries = timeSince(lastSuccessfulUpdate) > UpdatePolicy.downloadEverythingInterval();

            WhatHasToBeUpdated result = new WhatHasToBeUpdated();
            if (shouldUpdateAllSeries) {
                result.seriesWithDataToUpdate = followedSeries;
                result.seriesWithPosterToUpdate = followedSeries;
            } else {
                // TODO(Gabriel): SeriesSource should return all the information regarding update
                // of series since someday at once in fetchUpdateMetadataSince
                this.fetchUpdateMetadataSince(lastSuccessfulUpdate);

                CollectionFilter<Series> withOutdatedData =
                        new CollectionFilter<Series>(new SeriesIdInCollectionSpecification(
                                seriesSource.seriesUpdateMetadata()));

                CollectionFilter<Series> withOutdatedPoster =
                        new CollectionFilter<Series>(new SeriesIdInCollectionSpecification(
                                seriesSource.posterUpdateMetadata().keySet()));


                result.seriesWithDataToUpdate = withOutdatedData.in(followedSeries);
                result.seriesWithPosterToUpdate = withOutdatedPoster.in(followedSeries);

                // Series whose posters are not downloaded.
                CollectionFilter<Series> whosePosterIsNotDownloaded =
                        new CollectionFilter<Series>(new AbstractSpecification<Series>() {
                            @Override
                            public boolean isSatisfiedBy(Series s) {
                                return posterAvailableButNotDownloaded(s);
                            }
                        });

                result.seriesWithPosterToUpdate.addAll(whosePosterIsNotDownloaded.in(followedSeries));
            }

            return result;
        }

        private void fetchUpdateMetadataSince(long lastSuccessfulUpdate) throws Exception {
            try {
                FetchUpdateMetadataTask fetchUpdateMetadataTask =
                        new FetchUpdateMetadataTask(seriesSource, lastSuccessfulUpdate);

                Future<?> future = executor.submit(fetchUpdateMetadataTask);
                future.get(UpdatePolicy.updateTimeout(), UpdatePolicy.updateTimeoutUnit());

                if (!fetchUpdateMetadataTask.result().success()) {
                    throw fetchUpdateMetadataTask.result().error();
                }
            } catch (InterruptedException e) {
                throw e;
            } catch (ExecutionException e) {
                throw (Exception) e.getCause();
            } catch (TimeoutException e) {
                throw new UpdateTimeoutException(e);
            }
        }

        private Map<Series, Exception> updateAllSeriesIn(WhatHasToBeUpdated whatHasToBeUpdated) {
            Set<Series> seriesToBeUpdated = whatHasToBeUpdated.seriesToBeUpdated();

            final int totalNumberOfUpdates = seriesToBeUpdated.size();
            int currentUpdate = 1;

            Map<Series, Exception> errors = new HashMap<Series, Exception>();

            // Even though we are only going to update series that are in seriesToBeUpdated, we also want to update
            // each series' lastUpdate field, so we can keep the information that this series didn't have any update
            // until now. It allows us to save data transfer by downloading shorter update data given that the earliest
            // update date of all followed series will be later after this process.
            for (final Series s : followedSeries()) {
                if (seriesToBeUpdated.contains(s)) {
                    notifyListenersOfUpdateProgress(currentUpdate++, totalNumberOfUpdates, s);
                }
                try {
                    // Update data of series
                    if (whatHasToBeUpdated.seriesWithDataToUpdate.contains(s)) {
                        Log.d(getClass().getName(), "Updating data of " + s.name());
                        UpdateResult result = updateDataOf(s);

                        if (!result.success()) {
                            errors.put(s, result.error());
                            continue;
                        }
                    } else {
                        Log.d(getClass().getName(), "Skip updating data of " + s.name());
                    }

                    // Update poster of series
                    if (whatHasToBeUpdated.seriesWithPosterToUpdate.contains(s)) {
                        Log.d(getClass().getName(), "Updating poster of " + s.name());
                        UpdateResult result = updatePosterOf(s); 

                        if (!result.success()) {
                            errors.put(s, result.error());
                            continue;
                        }
                    } else {
                        Log.d(getClass().getName(), "Skip updating poster of " + s.name());
                    }

                    s.setLastUpdate(System.currentTimeMillis());
                    seriesRepository.update(s);

                } catch (InterruptedException e) {
                    // Should never happen
                    errors.put(s, e);

                } catch (ExecutionException e) {
                    errors.put(s, (Exception) e.getCause());

                } catch (TimeoutException e) {
                    errors.put(s, new UpdateTimeoutException(e));

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
    }
}
