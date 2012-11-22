package mobi.myseries.update;

/*
 * TODO (Reul): verify if connection is of the right type
 * TODO (Reul): add a 30s timeout to each series update
 * TODO (Reul): create one AsyncTask per update task
 */

import java.util.Collection;

import mobi.myseries.application.App;
import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.SettingsProvider;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import mobi.myseries.shared.CollectionFilter;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class UpdateService implements Publisher<UpdateListener> {
    private static final long AUTOMATIC_UPDATE_INTERVAL =  12L * 60L * 60L * 1000L;
    private static final long ONE_MONTH = 30L * 24L * 60L * 60L * 1000L;
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private LocalizationProvider localizationProvider;
    private ImageService imageService;
    private Update update;
    private final ListenerSet<UpdateListener> updateListeners;
    private boolean updateRunning = false;
    private UpdateListener selfListener;

    public UpdateService(SeriesSource seriesSource, SeriesRepository seriesRepository,
            LocalizationProvider localizationProvider, ImageService imageService) {

        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageService, "imageService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageService = imageService;
        update = new Update();
        updateListeners = new ListenerSet<UpdateListener>();

        selfListener = new UpdateListener() {

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

    public void updateData(Handler handler) {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }

        update.run(handler);
    }

    public void updateDataIfNeeded(Handler handler) {
        if (updateRunning) {
            Log.d(getClass().getName(), "Update already running");
            return;
        }
        if (!shouldUpdate()) {
            Log.d(getClass().getName(), "Update will not run.");
            return;
        }

        long lastSuccessfulUpdate = earliestUpdatedDateOf(seriesRepository.getAll());

        if (timeSince(lastSuccessfulUpdate) < AUTOMATIC_UPDATE_INTERVAL) {
            Log.d(getClass().getName(), "Update ran recently. Not running now.");

        } else {
            update.run(handler);
            Log.d(getClass().getName(), "Launching update.");
        }
    }

    private void notifyListenersOfUpdateStart(Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateStart();
                }
            }
        });
    }

    private void notifyListenersOfUpdateNotNecessary(Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateNotNecessary();
                }
            }
        });
    }

    private void notifyListenersOfUpdateSuccess(Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateSuccess();
                }
            }
        });
    }

    private void notifyListenersOfUpdateFailure(final Exception cause, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (final UpdateListener listener : updateListeners) {
                    listener.onUpdateFailure(cause);
                }
            }
        });
    }

    public boolean isUpdating() {
        return updateRunning;
    }

    private class Update {
        public void run(Handler handler) {
            run(handler, true);
        }

        public void run(Handler handler, boolean forceUpdateRecent) {
            notifyListenersOfUpdateStart(handler);

            if (!networkAvailable()) {
                notifyListenersOfUpdateFailure(new NetworkUnavailableException(), handler);
                return;
            }

            try {

                boolean updateAvailable = false;

                Collection<Series> seriesWithDataToUpdate = followedSeries();
                Collection<Series> seriesWithPosterToUpdate = followedSeries();

                long lastSuccessfulUpdate = earliestUpdatedDateOf(followedSeries());
                if (timeSince(lastSuccessfulUpdate) < ONE_MONTH) {

                    updateAvailable =
                            fetchUpdateMetadataSince(lastSuccessfulUpdate);

                    CollectionFilter<Series> withOutdatedData =
                            new CollectionFilter<Series>(
                                    new SeriesIdInCollectionSpecification(
                                            seriesSource.seriesUpdateMetadata()));

                    CollectionFilter<Series> withOutdatedPoster =
                            new CollectionFilter<Series>(
                                    new SeriesIdInCollectionSpecification(seriesSource
                                            .posterUpdateMetadata().keySet()));

                    seriesWithDataToUpdate = withOutdatedData.in(followedSeries());
                    seriesWithPosterToUpdate =
                            withOutdatedPoster.in(followedSeries());

                    if (!forceUpdateRecent) {
                        CollectionFilter<Series> notRecentlyUpdated =
                                new CollectionFilter<Series>(new RecentlyUpdatedSpecification());

                        seriesWithDataToUpdate = notRecentlyUpdated.in(seriesWithDataToUpdate);
                        seriesWithPosterToUpdate = notRecentlyUpdated.in(seriesWithPosterToUpdate);
                    }
                }

                if (!updateAvailable ||
                        ((seriesWithDataToUpdate.size() == 0)
                        && (seriesWithPosterToUpdate.size() == 0))) {
                    notifyListenersOfUpdateNotNecessary(handler);
                }

                for (final Series s : followedSeries()) {

                    if (seriesWithDataToUpdate.contains(s)) {
                        updateDataOf(s);
                        Log.d(getClass().getName(), "Data of " + s.name()
                                + " updated.");
                    }

                    if ((s.hasPoster() && imageService.getPosterOf(s) == null)
                            || seriesWithPosterToUpdate.contains(s)) {
                        updatePosterOf(s, handler);
                        Log.d(getClass().getName(), "Poster of " + s.name()
                                + " updated. (" + s.posterFileName() + ") ");
                    }

                    s.setLastUpdate(System.currentTimeMillis());
                    seriesRepository.update(s);
                }

                notifyListenersOfUpdateSuccess(handler);

            } catch (ConnectionFailedException e) {
                notifyListenersOfUpdateFailure(e, handler);
                e.printStackTrace();

            } catch (ConnectionTimeoutException e) {
                notifyListenersOfUpdateFailure(e, handler);
                e.printStackTrace();

            } catch (ParsingFailedException e) {
                notifyListenersOfUpdateFailure(e, handler);
                e.printStackTrace();

            } catch (UpdateMetadataUnavailableException e) {
                notifyListenersOfUpdateFailure(e, handler);
                e.printStackTrace();

            } catch (SeriesNotFoundException e) {
                notifyListenersOfUpdateFailure(e, handler);
                e.printStackTrace();

            }
        }
    }

    private static long timeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
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

    private void updatePosterOf(final Series series, Handler handler) {
        Log.d(getClass().getName(), "Downloading poster of " + series.name());
        handler.post(new Runnable() {
            @Override
            public void run() {
                series.setPosterFilename(seriesSource.posterUpdateMetadata().get(series.id()));
                imageService.downloadPosterOf(series);
            }
        });
    }

    private boolean fetchUpdateMetadataSince(long dateInMiliseconds)
            throws ConnectionFailedException, ConnectionTimeoutException,
            ParsingFailedException, UpdateMetadataUnavailableException {
        return seriesSource.fetchUpdateMetadataSince(dateInMiliseconds);
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

    private boolean shouldUpdate() {
        SettingsProvider settings = new SettingsProvider(App.context());

        if (!settings.updateAutomatically()) {
            Log.d(this.getClass().getName(), "Do not update automatically.");
            return false;
        }

        NetworkInfo networkInfo = this.activeNetworkInfo();

        if ((networkInfo == null) || !networkInfo.isConnected()) {
            Log.d(this.getClass().getName(), "No connection.");
            return false;

        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            Log.d(this.getClass().getName(), "Update on data plan? " + settings.updateOnDataPlan());
            return settings.updateOnDataPlan();
        }

        return true;
    }

    private boolean networkAvailable() {
        return (activeNetworkInfo() != null) && activeNetworkInfo().isConnected();
    }

    private NetworkInfo activeNetworkInfo() {
        ConnectivityManager connectivityManager =
                ((ConnectivityManager) App.context().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public boolean register(UpdateListener listener) {
        return updateListeners.register(listener);
    }

    @Override
    public boolean deregister(UpdateListener listener) {
        return updateListeners.deregister(listener);
    }

    public static long automaticUpdateInterval() {
        return AUTOMATIC_UPDATE_INTERVAL;
    }

}
