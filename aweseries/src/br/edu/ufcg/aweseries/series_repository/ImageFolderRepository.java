/*
 *   ImageProvider.java
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

package br.edu.ufcg.aweseries.series_repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.edu.ufcg.aweseries.series_repository.exceptions.ExternalStorageNotAvailableException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class ImageFolderRepository implements ImageRepository {

    private final CompressFormat IMAGE_FORMAT = CompressFormat.JPEG;
    private final String IMAGE_EXTENSION = "." + IMAGE_FORMAT.toString().toLowerCase();
    private final int COMPRESS_QUALITY = 85;

    private File seriesPostersFolder;
    private File episodeImagesFolder;

    public static ImageFolderRepository newInstance() {
        return new ImageFolderRepository();
    }

    @Override
    public void insertSeriesPoster(int seriesId, Bitmap file) {
        this.seriesPostersFolder = ImageFolder.SERIES_POSTERS.directory();
        File poster = new File(seriesPostersFolder, seriesId + IMAGE_EXTENSION);
        saveImageFile(file, poster);

    }

    @Override
    public void insertEpisodeImage(int episodeId, Bitmap file) {
        this.episodeImagesFolder = ImageFolder.EPISODE_IMAGES.directory();
        File episodeImage = new File(episodeImagesFolder, episodeId + IMAGE_EXTENSION);
        saveImageFile(file, episodeImage);
    }

    @Override
    public void updateSeriesPoster(int seriesId, Bitmap file) {
        this.seriesPostersFolder = ImageFolder.SERIES_POSTERS.directory();
        this.deleteSeriesPoster(seriesId);
        this.insertSeriesPoster(seriesId, file);
    }

    @Override
    public void updateEpisodeImage(int episodeId, Bitmap file) {
        this.episodeImagesFolder = ImageFolder.EPISODE_IMAGES.directory();
        this.deleteEpisodeImage(episodeId);
        this.insertEpisodeImage(episodeId, file);
    }

    @Override
    public void deleteSeriesPoster(int seriesId) {
        this.seriesPostersFolder = ImageFolder.SERIES_POSTERS.directory();
        File poster = new File(seriesPostersFolder, seriesId + IMAGE_EXTENSION);
        if(!poster.delete()) {
            throw new ImageIoException("write", poster.toString());
        }
    }

    @Override
    public void deleteEpisodeImage(int episodeId) {
        File episodeImage = new File(episodeImagesFolder, episodeId + IMAGE_EXTENSION);
        if(!episodeImage.delete()) {
            throw new ImageIoException("delete", episodeImage.toString());
        }
    }

    @Override
    public Bitmap getSeriesPoster(int seriesId) {
        try {
            this.seriesPostersFolder = ImageFolder.SERIES_POSTERS.directory();
        } catch (ExternalStorageNotAvailableException e) {
            return null;
        }
        return BitmapFactory.decodeFile(this.seriesPostersFolder
                + System.getProperty("file.separator") + seriesId + IMAGE_EXTENSION);

    }

    @Override
    public Bitmap getEpisodeImage(int episodeId) {
        try {
            this.episodeImagesFolder = ImageFolder.EPISODE_IMAGES.directory();
        } catch (ExternalStorageNotAvailableException e) {
            return null;
        }
        return BitmapFactory.decodeFile(this.episodeImagesFolder
                + System.getProperty("file.separator") + episodeId + IMAGE_EXTENSION);
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
}
