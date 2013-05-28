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

import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.image.AndroidImageServiceRepository;
import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.repository.series.SeriesCache;
import mobi.myseries.domain.repository.series.SeriesDatabase;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.TheTVDB;
import mobi.myseries.domain.source.TraktTv;
import mobi.myseries.domain.source.TrendingSource;
import mobi.myseries.shared.Validate;
import android.content.Context;

public class Environment {
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    private static final String THE_TVDB_API_KEY = "6F2B5A871C96FB05";
    private static final String TRAKTTV_API_KEY = "2665c5546c888a02c4ceff0afccfa927";    // Replace this with your app key and secret assigned by Dropbox.
    private static String DROPBOX_APP_KEY = "16plq57cyv3mxdb";
    private static String DROPBOX_APP_SECRET = "5z6c5a0ku03kyjy";
    
    private TheTVDB theTVDB;
    private TraktTv traktTv;
    private DropboxHelper dropboxHelper;
    private LocalizationProvider localizationProvider;
    private SeriesRepository seriesRepository;
    private ImageServiceRepository imageRepository;

    private Context context;

    public Environment(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;

        this.theTVDB = new TheTVDB(THE_TVDB_API_KEY);
        this.traktTv = new TraktTv(TRAKTTV_API_KEY);
        this.dropboxHelper = new DropboxHelper(this.context, DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
        this.localizationProvider =  new AndroidLocalizationProvider();
        this.seriesRepository = new SeriesCache(new SeriesDatabase(this.context));
        this.imageRepository = new AndroidImageServiceRepository(this.context);
    }

    public Context context() {
        return this.context;
    }

    public SeriesSource seriesSource() {
        return this.theTVDB;
    }

    public TrendingSource trendingSource() {
        return this.traktTv;
    }

    public ImageSource imageSource() {
        return this.theTVDB;
    }

    public DropboxHelper dropboxHelper() {
        return this.dropboxHelper;
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
