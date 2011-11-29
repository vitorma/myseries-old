/*
 *   UrlSupplier.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.thetvdb.stream.url;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import br.edu.ufcg.aweseries.thetvdb.parsing.MirrorsParser;
import br.edu.ufcg.aweseries.util.Strings;

public class UrlSupplier {
    private String apiKey;
    private Mirrors mirrors;

    public UrlSupplier(String apiKey) {
        this.apiKey = apiKey;
    }

    //MIRRORS---------------------------------------------------------------------------------------

    private String getMirrorUrl() {
        return "http://thetvdb.com/api/" + this.apiKey + "/mirrors.xml";
    }

    private void loadMirrors() {
        this.mirrors = new MirrorsParser(streamFor(this.getMirrorUrl())).parse();
    }

    private InputStream streamFor(String url) {
        try {
            return new URL(url).openConnection().getInputStream();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMirrorPath(MirrorType type) {
        if (this.mirrors == null) {
            this.loadMirrors();
        }

        return this.mirrors.getRandomMirror(type).getPath();
    }

    private StringBuilder getXmlUrl() {
        return new StringBuilder(this.getMirrorPath(MirrorType.XML))
                .append("/api/").append(this.apiKey);
    }

	private StringBuilder getBannerUrl() {
        return new StringBuilder(this.getMirrorPath(MirrorType.BANNER))
                .append("/banners/");
    }

    @SuppressWarnings("unused")
    private StringBuilder getZipUrl() {
        return new StringBuilder(this.getMirrorPath(MirrorType.ZIP))
                .append("/api/").append(this.apiKey);
    }

    //SERIES ---------------------------------------------------------------------------------------

    private StringBuilder getSeriesUrlBuilder(String id) {
        return this.getXmlUrl().append("/series/").append(id);
    }

    public String getBaseSeriesUrl(String id) {
        return this.getSeriesUrlBuilder(id).toString();
    }

    public String getFullSeriesUrl(String id) {
        return getSeriesUrlBuilder(id).append("/all/").toString();
    }

    public String getFullSeriesUrl(String id, String language) {
        return getSeriesUrlBuilder(id)
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
        if (filename == null || Strings.isBlank(filename)) {
            return null;
        }

        return this.getBannerUrl().append(filename).toString();
    }
}