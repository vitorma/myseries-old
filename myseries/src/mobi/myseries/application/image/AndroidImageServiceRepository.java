/*
 *   AndroidImageServiceRepository.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.application.image;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepositoryCache;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.domain.repository.image.LruRepositoryManager;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class AndroidImageServiceRepository implements ImageServiceRepository {
    private static final String SERIES_POSTERS = "series_posters";
    private static final String EPISODE_IMAGES = "episode_images";

    private static final String LOG_TAG = "Image Service Repository";

    private static final int KiB = 1024;
    private static final int MiB = 1024 * KiB;
    private static final int EPISODE_IMAGE_AVERAGE_SIZE = 14 * KiB;
    private static final int EPISODE_IMAGE_CACHE_SIZE = 1 * MiB;
    private static final int NUMBER_OF_EPISODE_IMAGE_CACHE_ENTRIES = EPISODE_IMAGE_CACHE_SIZE /
                                                                             EPISODE_IMAGE_AVERAGE_SIZE;

    private final ImageRepository posterDirectory;
    private final ImageRepository episodeDirectory;

    public AndroidImageServiceRepository(Context context) {
        Validate.isNonNull(context, "context");

        this.posterDirectory = new ImageRepositoryCache(new ExternalStorageImageDirectory(context, SERIES_POSTERS));

        this.episodeDirectory = new LruRepositoryManager(new ExternalStorageImageDirectory(context, EPISODE_IMAGES),
                                                         NUMBER_OF_EPISODE_IMAGE_CACHE_ENTRIES);
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

    private void deleteEpisodeImage(Episode episode) {
        Log.d(LOG_TAG, "Deleting image of episode " + episode.name());

        try {
            this.episodeDirectory.delete(episode.id());
        } catch (ImageRepositoryException e) {
            Log.w(LOG_TAG, "Failed deleting image of episode " + episode.name(), e);
        }
    }
}