/*
 *   UrlSupplier.java
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

package br.edu.ufcg.aweseries.thetvdb.stream.url;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import br.edu.ufcg.aweseries.thetvdb.Language;
import br.edu.ufcg.aweseries.util.Strings;
import br.edu.ufcg.aweseries.util.Validate;

public class UrlSupplier {
    private static final String MIRROR = "http://thetvdb.com";

    private String apiKey;

    //Construction------------------------------------------------------------------------------------------------------

    public UrlSupplier(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey should be non-null");
        Validate.isTrue(!Strings.isBlank(apiKey), "apiKey should be non-blank");

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
        Validate.isNonNull(language, "language should be non-null");

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
        Validate.isNonNull(seriesName, "seriesName should be non-null");
        Validate.isNonNull(language, "language should be non-null");
        Validate.isTrue(!Strings.isBlank(seriesName), "seriesName should be non-blank");

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

    public URL urlForPoster(String fileName) {
        Validate.isNonNull(fileName, "fileName should be non-null");
        Validate.isTrue(!Strings.isBlank(fileName), "fileName should be non-blank");

        String url = this.buildUrlForPoster(fileName);

        return this.urlFrom(url);
    }

    private String buildUrlForPoster(String fileName) {
        return this.mirrorBanners().append(this.encode(fileName)).toString();
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
