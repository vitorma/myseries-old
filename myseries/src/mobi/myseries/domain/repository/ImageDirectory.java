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
    public void saveSeriesPoster(int seriesId, Bitmap file) {
        Validate.isNonNull(file, "series poster");

        File seriesPostersFolder = this.imageFolder(SERIES_POSTERS);
        File poster = new File(seriesPostersFolder, seriesId + IMAGE_EXTENSION);
        this.saveImageFile(file, poster);
    }

    @Override
    public void saveEpisodeImage(int episodeId, Bitmap file) {
        Validate.isNonNull(file, "episode image");

        File episodeImagesFolder = this.imageFolder(EPISODE_IMAGES);
        File episodeImage = new File(episodeImagesFolder, episodeId + IMAGE_EXTENSION);
        this.saveImageFile(file, episodeImage);
    }

    @Override
    public void deleteAllSeriesImages(int seriesId) {
        File seriesPostersFolder = this.imageFolder(SERIES_POSTERS);
        File poster = new File(seriesPostersFolder, seriesId + IMAGE_EXTENSION);
        if (!poster.delete()) {
            throw new ImageIoException("write", poster.toString());
        }
    }

    private void deleteEpisodeImage(int episodeId) {
        File episodeImagesFolder = this.imageFolder(EPISODE_IMAGES);
        File episodeImage = new File(episodeImagesFolder, episodeId + IMAGE_EXTENSION);
        if (!episodeImage.delete()) {
            throw new ImageIoException("delete", episodeImage.toString());
        }
    }

    @Override
    public Bitmap getSeriesPoster(int seriesId) {
        File seriesPostersFolder = this.imageFolder(SERIES_POSTERS);
        return BitmapFactory.decodeFile(seriesPostersFolder + FILE_SEPARATOR + seriesId + IMAGE_EXTENSION);
    }

    @Override
    public Bitmap getEpisodeImage(int episodeId) {
        File episodeImagesFolder = this.imageFolder(EPISODE_IMAGES);
        return BitmapFactory.decodeFile(episodeImagesFolder + FILE_SEPARATOR + episodeId + IMAGE_EXTENSION);
    }

    private void saveImageFile(Bitmap image, File file) {
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
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
//        if(!this.isAvaliable())
//            throw new ExternalStorageNotAvailableException();
        //TODO create a shared preference to select internal or external storage
        //if(App.environment().context().getSharedPreferences("STORAGE_MODE", 0).equals(EXTERNAL))
        return this.context.getExternalFilesDir(null);
        //return App.environment().context().getFilesDir();
    }

    private boolean isAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getPathForPoster(int series) {
        return App.environment().context().getExternalFilesDir(null) + FILE_SEPARATOR + SERIES_POSTERS +FILE_SEPARATOR + series + IMAGE_EXTENSION;

    }
}
