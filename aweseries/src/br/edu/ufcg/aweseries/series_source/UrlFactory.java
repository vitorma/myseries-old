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

package br.edu.ufcg.aweseries.series_source;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import br.edu.ufcg.aweseries.util.Validate;

public class UrlFactory {
    private static final String MIRROR = "http://thetvdb.com";

    private String apiKey;

    //Construction------------------------------------------------------------------------------------------------------

    public UrlFactory(String apiKey) {
        Validate.isNonBlank(apiKey, "apiKey");

        this.apiKey = apiKey;
    }

    //Mirror------------------------------------------------------------------------------------------------------------

    private StringBuilder mirrorXml() {
        return new StringBuilder(MIRROR).append("/api/");
    }

    private StringBuilder mirrorBanners() {
        return new StringBuilder(MIRROR).append("/banners/");
    }

    //Series------------------------------------------------------------------------------------------------------------

    public URL urlForSeries(int seriesId, Language language) {
        Validate.isNonNull(language, "language");

        String url = this.urlForSeries(seriesId, language.abbreviation());

        return this.urlFrom(url);
    }

    private String urlForSeries(int seriesId, String language) {
        return this.mirrorXml()
                   .append(this.apiKey)
                   .append("/series/")
                   .append(seriesId)
                   .append("/all/")
                   .append(language)
                   .append(".xml")
                   .toString();
    }

    public URL urlForSeriesSearch(String seriesName, Language language) {
        Validate.isNonBlank(seriesName, "seriesName");
        Validate.isNonNull(language, "language");

        String url = this.urlForSeriesSearch(seriesName, language.abbreviation());

        return this.urlFrom(url);
    }

    private String urlForSeriesSearch(String seriesName, String language) {
        return this.mirrorXml()
                   .append("GetSeries.php?seriesname=")
                   .append(this.encode(seriesName))
                   .append("&language=")
                   .append(language)
                   .toString();
    }

    //Image-------------------------------------------------------------------------------------------------------------

    public URL urlForSeriesPoster(String fileName) {
        Validate.isNonBlank(fileName, "fileName");

        String url = this.buildUrlForSeriesPoster(fileName);

        return this.urlFrom(url);
    }

    private String buildUrlForSeriesPoster(String fileName) {
        return this.mirrorBanners().append("_cache/").append(this.encode(fileName)).toString();
    }

    //URL---------------------------------------------------------------------------------------------------------------

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException should never be thrown by UrlSupplier");
        }
    }

    private URL urlFrom(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURLException should never be thrown by UrlSupplier");
        }
    }
}
