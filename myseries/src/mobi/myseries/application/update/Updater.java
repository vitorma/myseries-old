package mobi.myseries.application.update;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import android.util.Log;

/*
 * TODO (Reul): add a 30s timeout to each update.
 */

class Updater {
    private LocalizationProvider localizationProvider;
    private ImageService imageService;
    private SeriesSource seriesSource;

    Updater(SeriesSource seriesSource, LocalizationProvider localizationProvider,
            ImageService imageService) {
        this.seriesSource = seriesSource;
        this.localizationProvider = localizationProvider;
        this.imageService = imageService;
    }

    UpdateResult updateDataOf(final Series series) {
        Log.d(getClass().getName(),
                "Updating series: " + series.name());

        Log.d(getClass().getName(), "Last updated: " + series.lastUpdate());

        Series downloadedSeries;

        Log.d(getClass().getName(), "Updating " + series.name());

        Exception error = null;

        try {
            downloadedSeries = seriesSource.fetchSeries(series.id(),
                    localizationProvider.language());
            series.seasons().turnNotificationsOff();
            series.mergeWith(downloadedSeries);
            series.seasons().turnNotificationsOn();

            Log.d(getClass().getName(), "Data of " + series.name() + " updated.");

        } catch (Exception e) {
            e.printStackTrace();
            error = e;
        }

        return new UpdateResult().withError(error);

    }

    void updatePosterOf(final Series series) {
        Log.d(getClass().getName(), "Downloading poster of " + series.name());
        imageService.downloadAndSavePosterOf(series);

        Log.d(getClass().getName(),
                "Poster of " + series.name() + " updated. (" + series.posterFileName() + ") ");
    }

    boolean fetchUpdateMetadataSince(long dateInMiliseconds)
            throws ConnectionFailedException, ConnectionTimeoutException,
            ParsingFailedException, UpdateMetadataUnavailableException {
        return seriesSource.fetchUpdateMetadataSince(dateInMiliseconds);
    }

}
