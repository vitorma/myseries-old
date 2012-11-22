/*
 *   Environment.java
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

package mobi.myseries.application;

import mobi.myseries.application.image.AndroidImageStorage;
import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.repository.series.SeriesCache;
import mobi.myseries.domain.repository.series.SeriesDatabase;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.TheTVDB;
import mobi.myseries.shared.Validate;
import android.content.Context;

public class Environment {
    private static final String THE_TVDB_API_KEY = "6F2B5A871C96FB05";

    private TheTVDB theTVDB;
    private LocalizationProvider localizationProvider;
    private SeriesRepository seriesRepository;
    private ImageServiceRepository imageRepository;

    private Context context;

    public static Environment newEnvironment(Context context) {
        return new Environment(context);
    }

    private Environment(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;

        this.theTVDB = new TheTVDB(THE_TVDB_API_KEY);
        this.localizationProvider =  new AndroidLocalizationProvider();
        this.seriesRepository = new SeriesCache(new SeriesDatabase(this.context));
        this.imageRepository = new AndroidImageStorage(this.context);
    }

    public Context context() {
        return this.context;
    }

    public SeriesSource seriesSource() {
        return this.theTVDB;
    }

    public ImageSource imageSource() {
        return this.theTVDB;
    }

    public LocalizationProvider localizationProvider() {
        return this.localizationProvider;
    }

    public SeriesRepository seriesRepository() {
        return this.seriesRepository;
    }

    public ImageServiceRepository imageRepository() {
        return this.imageRepository;
    }
}
