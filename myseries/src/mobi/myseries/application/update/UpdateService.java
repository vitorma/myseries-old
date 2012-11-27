package mobi.myseries.application.update;

import java.util.Collection;

import mobi.myseries.application.LocalizationProvider;
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

public class UpdateService implements Publisher<UpdateListener> {
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private ImageService imageService;
    private final ListenerSet<UpdateListener> updateListeners;
    private boolean updateRunning = false;
    private UpdateListener selfListener;
    private Updater updater;
    private Handler handler;

    public UpdateService(SeriesSource seriesSource, SeriesRepository seriesRepository,
            LocalizationProvider localizationProvider, ImageService imageService) {

        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageService, "imageService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.imageService = imageService;
        this.updateListeners = new ListenerSet<UpdateListener>();
        this.updater = new Updater(seriesSource, localizationProvider, imageService);

        this.selfListener = new UpdateListener() {

            @Override
            public void onUpdateSuccess() {
                Log.d(getClass().getName(), "Update finished successfully. :)");
                updateRunning = false;
            }

            @Override
            public void onUpdateStart() {
                Log.d(getClass().getName(), "Update started.");
                updateRunning = true;
            }

            @Override
            public void onUpdateNotNecessary() {
                Log.d(getClass().getName(), "Update is not necessary.");
                updateRunning = false;
            }

            @Override
            public void onUpdateFailure(Exception e) {
                Log.d(getClass().getName(), "Update finished with failure. :(");
                updateRunning = false;

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
            update();
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

    // Auxiliary

    private void update() {
        this.update(false);
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
                updateAvailable = updater.fetchUpdateMetadataSince(lastSuccessfulUpdate);
                Log.d(getClass().getName(), "Update Metadata Available? " + updateAvailable);

            } catch (ConnectionFailedException e) {
                notifyListenersOfUpdateFailure(e);
                e.printStackTrace();
                return;

            } catch (ConnectionTimeoutException e) {
                notifyListenersOfUpdateFailure(e);
                e.printStackTrace();
                return;

            } catch (ParsingFailedException e) {
                notifyListenersOfUpdateFailure(e);
                e.printStackTrace();
                return;

            } catch (UpdateMetadataUnavailableException e) {
                notifyListenersOfUpdateFailure(e);
                e.printStackTrace();
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

        for (final Series s : followedSeries()) {

            if (seriesWithDataToUpdate.contains(s)) {
                UpdateResult result = UpdateService.this.updater.updateDataOf(s);

                if (!result.success()) {
                    notifyListenersOfUpdateFailure(result.error());
                    return;
                }
            }

            if (posterAvailableButNotDownloaded(s) || seriesWithPosterToUpdate.contains(s)) {
                UpdateService.this.updater.updatePosterOf(s);
            }

            s.setLastUpdate(System.currentTimeMillis());
            seriesRepository.update(s);
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
            }
        });
    }
}
