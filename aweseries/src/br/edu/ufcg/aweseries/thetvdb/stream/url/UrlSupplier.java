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

    //Construction------------------------------------------------------------------------------------------------------

    public UrlSupplier(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey should be non-null");

        this.apiKey = apiKey;
    }

    //Mirror------------------------------------------------------------------------------------------------------------

    private static StringBuilder mirrorXml() {
        return new StringBuilder(MIRROR).append("/api/");
    }

    private static StringBuilder mirrorBanners() {
        return new StringBuilder(MIRROR).append("/banners/");
    }

    //Series------------------------------------------------------------------------------------------------------------

    public String urlForSeries(int seriesId, String language) {
        //TODO Check id and language after Series#id become an int and language become a Language
        //TODO Test after check be implemented

        String lang = language != null ? language + ".xml" : "";//TODO Remove it ASAP

        return mirrorXml().append(this.apiKey).append("/series/").append(seriesId).append("/all/").append(lang).toString();
    }

    public String urlForSeriesSearch(String seriesName, String language) {
        //TODO Check language after language become a Language
        //TODO Test after check be implemented

        String safeName = null;

        try {//TODO Move this block to higher level objects
            safeName = URLEncoder.encode(seriesName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO: a better exception handling
            return null;
        }

        String lang = language != null ? "&language=" + language : "";//TODO Remove it ASAP

        return mirrorXml().append("GetSeries.php?seriesname=").append(safeName).append(lang).toString();
    }

    //Image-------------------------------------------------------------------------------------------------------------

    public String urlForPoster(String fileName) {
        //TODO Check and throw
        if (fileName == null || Strings.isBlank(fileName))
            return null;

        return mirrorBanners().append(fileName).toString();
    }
}