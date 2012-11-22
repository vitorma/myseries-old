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

import mobi.myseries.domain.repository.ImageDirectory;
import mobi.myseries.domain.repository.ImageRepository;
import mobi.myseries.domain.repository.SeriesCache;
import mobi.myseries.domain.repository.SeriesDatabase;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.TheTVDB;
import mobi.myseries.shared.Validate;
import android.content.Context;

public class Environment {
    private Context context;
    private TheTVDB theTVDB;
    private SeriesProvider seriesProvider;
    private LocalizationProvider localization;
    private SeriesRepository seriesRepository;
    private ImageRepository imageRepository;

    private static final String apiKey = "6F2B5A871C96FB05";

    public static Environment newEnvironment(Context context) {
        return new Environment(context);
    }

    private Environment(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;
        this.seriesRepository = new SeriesCache(new SeriesDatabase(this.context));
        this.imageRepository = new ImageDirectory(this.context);
    }

    public Context context() {
        return this.context;
    }

    public SeriesProvider seriesProvider() {
        if (this.seriesProvider == null) {
            this.seriesProvider = this.defaultSeriesProvider();
        }

        return this.seriesProvider;
    }

    private SeriesProvider defaultSeriesProvider() {
        return SeriesProvider.newInstance(this.repository());
    }

    public void setSeriesProvider(SeriesProvider newSeriesProvider) {
        this.seriesProvider = newSeriesProvider;
    }

    public SeriesSource seriesSource() {
        return this.theTVDB();
    }

    public ImageSource imageSource() {
        return this.theTVDB();
    }

    // TODO Remove this method ASAP and use seriesSource() or imageSource()
    public TheTVDB theTVDB() {
        if (this.theTVDB == null) {
            this.theTVDB = this.defaultTheTVDB();
        }

        return this.theTVDB;
    }

    private TheTVDB defaultTheTVDB() {
        return new TheTVDB(apiKey);
    }

    public void setTheTVDBTo(TheTVDB newTheTVDB) {
        this.theTVDB = newTheTVDB;
    }

    public LocalizationProvider localization() {
        if (this.localization == null) {
            this.localization = this.defaultLocalization();
        }

        return this.localization;
    }

    private LocalizationProvider defaultLocalization() {
        return new AndroidLocalizationProvider();
    }

    public void setLocalizationTo(LocalizationProvider newLocalization) {
        this.localization = newLocalization;
    }

    public SeriesRepository repository() {
        if (this.seriesRepository == null) {
            this.seriesRepository = new SeriesCache(new SeriesDatabase(this.context()));
        }

        return this.seriesRepository;
    }

    public ImageRepository imageRepository() {
        return this.imageRepository;
    }
}
