/*
 *   UrlFactory.java
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

package mobi.myseries.domain.source;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import mobi.myseries.shared.Validate;

public class UrlFactory {
    private static final String MIRROR = "http://thetvdb.com";
    private static final String DAY = "day";
    private static final String WEEK = "week";
    private static final String MONTH = "month";
    private static final String ALL = "all";

    private final String apiKey;

    public UrlFactory(String apiKey) {
        Validate.isNonBlank(apiKey, "apiKey");

        this.apiKey = apiKey;
    }

    public URL urlForSeries(int seriesId, Language language) {
        Validate.isNonNull(language, "language");

        String url = this.buildUrlForSeries(seriesId, language.abbreviation());

        return this.urlFrom(url);
    }

    public URL urlForSeriesSearch(String seriesName, Language language) {
        Validate.isNonBlank(seriesName, "seriesName");
        Validate.isNonNull(language, "language");

        String url = this.buildUrlForSeriesSearch(seriesName, language.abbreviation());

        return this.urlFrom(url);
    }

    public URL urlForSeriesPoster(String fileName) {
        Validate.isNonBlank(fileName, "fileName");

        String url = this.buildUrlForSeriesPoster(fileName);

        return this.urlFrom(url);
    }

    public URL urlForEpisodeImage(String fileName) {
        Validate.isNonBlank(fileName, "fileName");

        String url = this.buildUrlForEpisodeImage(fileName);

        return this.urlFrom(url);
    }

    public URL urlForLastDayUpdates() {
        return this.urlFrom(buildUrlForUpdates(DAY));
    }

    public URL urlForLastWeekUpdates() {
        return this.urlFrom(buildUrlForUpdates(WEEK));
    }

    public URL urlForLastMonthUpdates() {
        return this.urlFrom(buildUrlForUpdates(MONTH));
    }

    public URL urlForAllAvailableUpdates() {
        return this.urlFrom(buildUrlForUpdates(ALL));
    }

    private StringBuilder mirrorXml() {
        return new StringBuilder(MIRROR).append("/api/");
    }

    private StringBuilder mirrorBanners() {
        return new StringBuilder(MIRROR).append("/banners/");
    }

    private String buildUrlForSeries(int seriesId, String language) {
        return this.mirrorXml()
                .append(this.apiKey)
                .append("/series/")
                .append(seriesId)
                .append("/all/")
                .append(language)
                .append(".xml")
                .toString();
    }

    private String buildUrlForSeriesSearch(String seriesName, String language) {
        return this.mirrorXml()
                .append("GetSeries.php?seriesname=")
                .append(this.encode(seriesName))
                .append("&language=")
                .append(language)
                .toString();
    }

    private String buildUrlForSeriesPoster(String fileName) {
        return this.mirrorBanners()
                .append("_cache/")
                .append(this.encode(fileName))
                .toString();
    }

    private String buildUrlForEpisodeImage(String fileName) {
        return this.mirrorBanners()
                .append("_cache/")
                .append(this.encode(fileName))
                .toString();
    }

    private String buildUrlForUpdates(String timespan) {
        return this.mirrorXml().append(this.apiKey).append("/updates/")
                .append("updates_" + timespan).append(".zip").toString();
    }

    private String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException should never be thrown by UrlFactory", e);
        }
    }

    private URL urlFrom(String string) {
        try {
            return new URL(string);
        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURLException should never be thrown by UrlFactory", e);
        }
    }
}
