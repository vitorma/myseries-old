/*
 *   ImageRepository.java
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

import android.graphics.Bitmap;

public interface ImageRepository {
    public void insertSeriesPoster(int seriesId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void insertEpisodeImage(int episodeId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void updateSeriesPoster(int seriesId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void updateEpisodeImage(int episodeId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void deleteImagesOfSeries(int seriesId) throws ExternalStorageNotAvailableException;
    public Bitmap getSeriesPoster(int seriesId) throws ExternalStorageNotAvailableException;
    public Bitmap getEpisodeImage(int episodeId) throws ExternalStorageNotAvailableException;
}
