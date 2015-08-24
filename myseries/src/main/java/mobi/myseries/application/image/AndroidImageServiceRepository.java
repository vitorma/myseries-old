package mobi.myseries.application.image;

import mobi.myseries.application.Log;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.image.DummyImageRepository;
import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class AndroidImageServiceRepository implements ImageServiceRepository {
    private static final String SERIES_POSTERS = "series_posters";

    private static final String LOG_TAG = "Image Service Repository";

    private final ImageRepository posterDirectory;

    public AndroidImageServiceRepository(Context context) {
        Validate.isNonNull(context, "context");

        this.posterDirectory =  new ExternalStorageImageDirectory(context, SERIES_POSTERS);
    }

    @Override
    public String getPosterOf(Series series) {
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
    public void deleteAllImagesOf(Series series) {
        Validate.isNonNull(series, "series");
        Log.d(LOG_TAG, "Deleting all images of " + series.name());

        new AsyncTask<Series, Void, Void>() {
            @Override
            protected Void doInBackground(Series... params) {
                Validate.isTrue(params.length == 1, "It must receive a single param", (Object) null);
                Series series = params[0];

                AndroidImageServiceRepository.this.deleteSeriesPoster(series);

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

    @Override
    public void clear() {
        Log.d(LOG_TAG, "Deleting all images");

        try {
            this.posterDirectory.clear();
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed deleting all images", e);
        }
        
    }
}
