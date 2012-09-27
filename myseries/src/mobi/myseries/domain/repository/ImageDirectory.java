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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageDirectory implements ImageRepository {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final CompressFormat IMAGE_FORMAT = CompressFormat.JPEG;
    private static final String IMAGE_EXTENSION = "." + IMAGE_FORMAT.toString().toLowerCase();
    private static final int COMPRESS_QUALITY = 85;
    private static final String SERIES_POSTERS = "series_posters";
    private static final String EPISODE_IMAGES = "episode_images";

    private Context context;

    public ImageDirectory(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;
    }

    @Override
    public void saveSeriesPoster(Series series, Bitmap file) {
        Validate.isNonNull(series, "series");
        Validate.isNonNull(file, "file");

        if(!this.isExternalStorageAvaliable()) {return;}

        File seriesPostersFolder = this.imageFolder(SERIES_POSTERS);
        File poster = new File(seriesPostersFolder, series.id() + IMAGE_EXTENSION);

        this.saveImageFile(file, poster);
    }

    @Override
    public void saveEpisodeImage(Episode episode, Bitmap file) {
        Validate.isNonNull(episode, "episode");
        Validate.isNonNull(file, "file");

        if(!this.isExternalStorageAvaliable()) {return;}

        File episodeImagesFolder = this.imageFolder(EPISODE_IMAGES);
        File episodeImage = new File(episodeImagesFolder, episode.id() + IMAGE_EXTENSION);

        this.saveImageFile(file, episodeImage);
    }

    @Override
    public void deleteAllImagesOf(Series series) {
        Validate.isNonNull(series, "series");

        if(!this.isExternalStorageAvaliable()) {return;}

        this.deleteSeriesPoster(series.id());

        for (Episode e : series.episodes()) {
            this.deleteEpisodeImage(e.id());
        }
    }

    private void deleteSeriesPoster(int seriesId) {
        File seriesPostersFolder = this.imageFolder(SERIES_POSTERS);
        File poster = new File(seriesPostersFolder, seriesId + IMAGE_EXTENSION);

        poster.delete();
    }

    private void deleteEpisodeImage(int episodeId) {
        File episodeImagesFolder = this.imageFolder(EPISODE_IMAGES);
        File episodeImage = new File(episodeImagesFolder, episodeId + IMAGE_EXTENSION);

        episodeImage.delete();
    }

    @Override
    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if(!this.isExternalStorageAvaliable()) {return null;}

        File seriesPostersFolder = this.imageFolder(SERIES_POSTERS);

        return BitmapFactory.decodeFile(seriesPostersFolder + FILE_SEPARATOR + series.id() + IMAGE_EXTENSION);
    }

    @Override
    public Bitmap getImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        if(!this.isExternalStorageAvaliable()) {return null;}

        File episodeImagesFolder = this.imageFolder(EPISODE_IMAGES);

        return BitmapFactory.decodeFile(episodeImagesFolder + FILE_SEPARATOR + episode.id() + IMAGE_EXTENSION);
    }

    private void saveImageFile(Bitmap image, File file) {
        try {
            FileOutputStream os = new FileOutputStream(file);
            image.compress(IMAGE_FORMAT, COMPRESS_QUALITY, os);
            os.close();
        } catch (IOException e) {
            throw new ImageIoException("create", file.toString());
        }
    }

    private static File ensuredDirectory(String path) {
        File directory = new File(path);

        try {
            if (!directory.exists()) {
                directory.mkdirs();
                File nomedia = new File(directory, ".nomedia");
                nomedia.createNewFile();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("can't create the given directory: " + path);
        } catch (IOException e) {
            throw new RuntimeException("can't write/read the on given directory: " + path);
        }

        return directory;
    }

    private File imageFolder(String folderName) {
        return ensuredDirectory(this.rootDirectory().getPath() + FILE_SEPARATOR + folderName);
    }

    private File rootDirectory() {
        return this.context.getExternalFilesDir(null);
    }

    private boolean isExternalStorageAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getPathForPoster(int seriesId) {
        return App.environment().context().getExternalFilesDir(null) +
               FILE_SEPARATOR +
               SERIES_POSTERS +
               FILE_SEPARATOR +
               seriesId +
               IMAGE_EXTENSION;
    }
}
