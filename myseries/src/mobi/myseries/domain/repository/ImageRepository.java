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
    public void saveSeriesPoster(int seriesId, Bitmap file);
    public void saveEpisodeImage(int episodeId, Bitmap file);
    public void deleteAllSeriesImages(int seriesId);
    public Bitmap getSeriesPoster(int seriesId);
    public Bitmap getEpisodeImage(int episodeId);
}
