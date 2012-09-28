/*
 *   ImageDirectory.java
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

package mobi.myseries.domain.repository;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageDirectory implements ImageRepository {
    private static final String SERIES_POSTERS = "series_posters";
    private static final String EPISODE_IMAGES = "episode_images";

    private class EpisodeImagesQueue extends LruCache<Integer, Integer> {
        private static final int KiB = 1024;
        private static final int MiB = 1024 * KiB;
        private static final int EPISODE_IMAGE_AVERAGE_SIZE = 14 * KiB;
        private static final int CACHE_SIZE = 1 * MiB;
        private static final int NUMBER_OF_CACHE_ENTRIES = CACHE_SIZE / EPISODE_IMAGE_AVERAGE_SIZE;

        public EpisodeImagesQueue() {
            super(NUMBER_OF_CACHE_ENTRIES);
        }

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Integer oldValue, Integer newValue) {
            if (evicted) {
                ImageDirectory.this.deleteEpisodeImage(key);
            }
        }
    }

    private final ExternalStorageImageDirectory posterDirectory;
    private final ExternalStorageImageDirectory episodeDirectory;

    private final EpisodeImagesQueue episodeImagesQueue;

    public ImageDirectory(Context context) {
        Validate.isNonNull(context, "context");

        this.posterDirectory = new ExternalStorageImageDirectory(context, SERIES_POSTERS);
        this.episodeDirectory = new ExternalStorageImageDirectory(context, EPISODE_IMAGES);

        this.episodeImagesQueue = new EpisodeImagesQueue();
    }

    @Override
    public void saveSeriesPoster(Series series, Bitmap file) {
        Validate.isNonNull(series, "series");

        this.posterDirectory.save(series.id(), file);
    }

    @Override
    public void saveEpisodeImage(Episode episode, Bitmap file) {
        Validate.isNonNull(episode, "episode");

        this.episodeDirectory.save(episode.id(), file);
        this.episodeImagesQueue.put(episode.id(), episode.id());
    }

    @Override
    public void deleteAllImagesOf(Series series) {
        Validate.isNonNull(series, "series");

        this.deleteSeriesPoster(series.id());

        for (Episode e : series.episodes()) {
            this.deleteEpisodeImage(e.id());
        }
    }

    private void deleteSeriesPoster(int seriesId) {
        this.posterDirectory.delete(seriesId);
    }

    private void deleteEpisodeImage(int episodeId) {
        this.episodeDirectory.delete(episodeId);
        this.episodeImagesQueue.remove(episodeId);
    }

    @Override
    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        return this.posterDirectory.fetch(series.id());
    }

    @Override
    public Bitmap getImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        Bitmap episodeImage = this.episodeDirectory.fetch(episode.id());

        if (episodeImage != null) {  // to avoid putting in the queue episodes without images and whose images cannot be
                                     // opened.
            this.episodeImagesQueue.put(episode.id(), episode.id());
        }

        return episodeImage;
    }
}
