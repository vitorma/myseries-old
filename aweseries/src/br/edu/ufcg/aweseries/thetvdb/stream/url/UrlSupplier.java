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
import java.net.URLEncoder;

import br.edu.ufcg.aweseries.util.Strings;
import br.edu.ufcg.aweseries.util.Validate;

public class UrlSupplier {
    private static final String MIRROR = "http://thetvdb.com";

    private String apiKey;

    public UrlSupplier(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey should be non-null");

        this.apiKey = apiKey;
    }

    private StringBuilder seriesUrlBuilder(String id) {
        return new StringBuilder(MIRROR).append("/api/").append(this.apiKey).append("/series/").append(id);
    }

    public String getBaseSeriesUrl(String id) {
        return this.seriesUrlBuilder(id).toString();
    }

    public String getFullSeriesUrl(String id) {
        return this.seriesUrlBuilder(id).append("/all/").toString();
    }

    public String getFullSeriesUrl(String id, String language) {
        return this.seriesUrlBuilder(id)
        .append("/all/").append((language != null ? "&language=" + language : "")).toString();
    }

    public String getSeriesSearchUrl(String name) {
        return "http://www.thetvdb.com/api/GetSeries.php?seriesname=" +
        name.trim().replaceAll("\\s+", "%20");
    }

    public String getSeriesSearchUrl(String name, String language) {
        try {
            return "http://www.thetvdb.com/api/GetSeries.php?seriesname=" +
            URLEncoder.encode(name, "UTF-8") +
            (language != null ? "&language=" + language : "");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    //POSTERS --------------------------------------------------------------------------------------

    public String getSeriesPosterUrl(String filename) {
        if (filename == null || Strings.isBlank(filename))
            return null;

        return new StringBuilder(MIRROR).append("/banners/").append(filename).toString();
    }
}