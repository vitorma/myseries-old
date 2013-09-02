package mobi.myseries.application.image;

import mobi.myseries.application.Log;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryCache;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.domain.repository.image.LruImageCache;
import mobi.myseries.domain.repository.image.LruRepositoryManager;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class AndroidImageServiceRepository implements ImageServiceRepository {
    private static final String SERIES_POSTERS = "series_posters";
    private static final String SMALL_SERIES_POSTERS = "small_series_posters";
    private static final String EPISODE_IMAGES = "episode_images";
    private static final String SERIES_BANNERS = "series_banners";

    private static final String LOG_TAG = "Image Service Repository";

    private static final int KiB = 1024;
    private static final int MiB = 1024 * KiB;
    private static final int EPISODE_IMAGE_AVERAGE_SIZE = 14 * KiB;
    private static final int EPISODE_IMAGE_CACHE_SIZE = 1 * MiB;
    private static final int NUMBER_OF_EPISODE_IMAGE_CACHE_ENTRIES = EPISODE_IMAGE_CACHE_SIZE /
                                                                             EPISODE_IMAGE_AVERAGE_SIZE;
    private static final float PERCENTAGE_OF_MEMORY_USED_IN_BIG_POSTER_CACHE = 0.125f;
    private static final float PERCENTAGE_OF_MEMORY_USED_IN_SMALL_POSTER_CACHE = 0.0625f;

    private final ImageRepository posterDirectory;
    private final ImageRepository smallPosterDirectory;
    private final ImageRepository episodeDirectory;
    private final ImageRepository bannerDirectory;

    public AndroidImageServiceRepository(Context context) {
        Validate.isNonNull(context, "context");

        final int deviceMemoryKiB = (int) (Runtime.getRuntime().maxMemory() / KiB);
        final int bigPosterCacheSizeKiB = (int) (deviceMemoryKiB * PERCENTAGE_OF_MEMORY_USED_IN_BIG_POSTER_CACHE);
        final int smallPosterCacheSizeKiB = (int) (deviceMemoryKiB * PERCENTAGE_OF_MEMORY_USED_IN_SMALL_POSTER_CACHE);
        
		this.posterDirectory = new LruImageCache(
				new ExternalStorageImageDirectory(context, SERIES_POSTERS),
				bigPosterCacheSizeKiB);

		this.smallPosterDirectory = new LruImageCache(
				new ExternalStorageImageDirectory(context, SMALL_SERIES_POSTERS),
				smallPosterCacheSizeKiB);

        this.episodeDirectory = new LruRepositoryManager(new ExternalStorageImageDirectory(context, EPISODE_IMAGES),
                                                         NUMBER_OF_EPISODE_IMAGE_CACHE_ENTRIES);

        this.bannerDirectory = new ExternalStorageImageDirectory(context, SERIES_BANNERS);
    }

    @Override
    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");
        Log.d(LOG_TAG, "Fetching poster of " + series.name());

        try {
            return this.posterDirectory.fetch(series.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed fetching poster of " + series.name(), e);
            return null;
        }
    }

    @Override
    public Bitmap getBannerOf(Series series) {
        Validate.isNonNull(series, "series");
        Log.d(LOG_TAG, "Fetching banner of " + series.name());

        try {
            return this.bannerDirectory.fetch(series.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed fetching banner of " + series.name(), e);
            return null;
        }
    }

    @Override
    public Bitmap getSmallPosterOf(Series series) {
        Validate.isNonNull(series, "series");
        Log.d(LOG_TAG, "Fetching small poster of " + series.name());

        try {
            return this.smallPosterDirectory.fetch(series.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed fetching small poster of " + series.name(), e);
            return null;
        }
    }

    @Override
    public Bitmap getImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");
        Log.d(LOG_TAG, "Fetching image of " + episode.name());

        try {
            return this.episodeDirectory.fetch(episode.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed fetching image of episode " + episode.name(), e);
            return null;
        }
    }

    @Override
    public void saveSeriesPoster(Series series, Bitmap poster) {
        Validate.isNonNull(series, "series");

        if (poster == null) {
            Log.d(LOG_TAG, "Skipped saving null poster for " + series.name());
            return;
        }

        Log.d(LOG_TAG, "Saving poster of " + series.name());
        try {
            this.posterDirectory.save(series.id(), poster);
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed saving poster of " + series.name(), e);
        }
    }

    @Override
    public void saveSeriesBanner(Series series, Bitmap banner) {
        Validate.isNonNull(series, "series");

        if (banner == null) {
            Log.d(LOG_TAG, "Skipped saving null banner for " + series.name());
            return;
        }

        Log.d(LOG_TAG, "Saving banner of " + series.name());
        try {
            this.bannerDirectory.save(series.id(), banner);
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed saving banner of " + series.name(), e);
        }
    }

    @Override
    public void saveSmallSeriesPoster(Series series, Bitmap smallPoster) {
        Validate.isNonNull(series, "series");

        if (smallPoster == null) {
            Log.d(LOG_TAG, "Skipped saving null small poster for " + series.name());
            return;
        }

        Log.d(LOG_TAG, "Saving small poster of " + series.name());
        try {
            this.smallPosterDirectory.save(series.id(), smallPoster);
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed saving small poster of " + series.name(), e);
        }
    }

    @Override
    public void saveEpisodeImage(Episode episode, Bitmap image) {
        Validate.isNonNull(episode, "episode");

        if (image == null) {
            Log.d(LOG_TAG, "Skipped saving null image for episode " + episode.name());
            return;
        }

        Log.d(LOG_TAG, "Saving image of episode " + episode.name());
        try {
            this.episodeDirectory.save(episode.id(), image);
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed saving image of episode " + episode.name(), e);
        }
    }

    @Override
    public void deleteAllImagesOf(Series series) {
        Validate.isNonNull(series, "series");
        Log.d(LOG_TAG, "Deleting all images of " + series.name());

        new AsyncTask<Series, Void, Void>() {
            @Override
            protected Void doInBackground(Series... params) {
                Validate.isTrue(params.length == 1, "It must receive a single param", (Object) null);
                Series series = params[0];

                AndroidImageServiceRepository.this.deleteSeriesPoster(series);
                AndroidImageServiceRepository.this.deleteSeriesBanner(series);

                AndroidImageServiceRepository.this.deleteSmallSeriesPoster(series);

                for (Episode e : series.episodes()) {
                    AndroidImageServiceRepository.this.deleteEpisodeImage(e);
                }

                return null;
            }
        }.execute(series);
    }

    private void deleteSeriesPoster(Series series) {
        Log.d(LOG_TAG, "Deleting poster of " + series.name());

        try {
            this.posterDirectory.delete(series.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed deleting poster of " + series.name(), e);
        }
    }

    private void deleteSeriesBanner(Series series) {
        Log.d(LOG_TAG, "Deleting banner of " + series.name());

        try {
            this.bannerDirectory.delete(series.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed deleting banner of " + series.name(), e);
        }
    }

    private void deleteSmallSeriesPoster(Series series) {
        Log.d(LOG_TAG, "Deleting small poster of " + series.name());

        try {
            this.smallPosterDirectory.delete(series.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed deleting small poster of " + series.name(), e);
        }
    }

    private void deleteEpisodeImage(Episode episode) {
        Log.d(LOG_TAG, "Deleting image of episode " + episode.name());

        try {
            this.episodeDirectory.delete(episode.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed deleting image of episode " + episode.name(), e);
        }
    }
}
