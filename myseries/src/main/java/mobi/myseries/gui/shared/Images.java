/*
 *   Images.java
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

package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.shared.Validate;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class Images {

    public static Bitmap genericSeriesPosterFrom(Resources resources) {
        return bitmapFrom(resources, R.drawable.generic_poster);
    }

    public static Bitmap genericSeriesPosterThumbnailFrom(Resources resources) {
        return bitmapFrom(resources, R.drawable.generic_poster_thumbnail);
    }

    public static Bitmap genericEpisodeImageFrom(Resources resources) {
        return bitmapFrom(resources, R.drawable.generic_episode_image);
    }

    private static Bitmap bitmapFrom(Resources resources, int resourceId) {
        Validate.isNonNull(resources, "resources");

        return BitmapFactory.decodeResource(resources, resourceId);
    }
}
