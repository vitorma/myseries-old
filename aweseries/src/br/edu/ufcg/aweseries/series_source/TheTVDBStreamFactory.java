/*
 *   TheTVDBStreamFactory.java
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class TheTVDBStreamFactory implements StreamFactory {
    private UrlFactory urlFactory;

    //Construction------------------------------------------------------------------------------------------------------

    public TheTVDBStreamFactory(String apiKey) {
        this.urlFactory = new UrlFactory(apiKey);
    }

    //Series------------------------------------------------------------------------------------------------------------

    @Override
    public InputStream streamForSeries(int seriesId, Language language) {
        URL url = this.urlFactory.urlForSeries(seriesId, language);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    @Override
    public InputStream streamForSeriesSearch(String seriesName, Language language) {
        URL url = this.urlFactory.urlForSeriesSearch(seriesName, language);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    //Image-------------------------------------------------------------------------------------------------------------

    @Override
    public InputStream streamForSeriesPoster(String fileName) {
        URL url = this.urlFactory.urlForSeriesPoster(fileName);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    @Override
    public InputStream streamForEpisodeImage(String fileName) {
        URL url = this.urlFactory.urlForEpisodeImage(fileName);
        return this.buffered(this.streamFrom(this.connectionTo(url)));
    }

    //Stream------------------------------------------------------------------------------------------------------------

    private BufferedInputStream buffered(InputStream stream) {
        return new BufferedInputStream(stream);
    }

    private InputStream streamFrom(URLConnection connection) {
        InputStream stream = null;

        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            throw new StreamCreationFailedException(e);
        }

        return stream;
    }

    //Connection--------------------------------------------------------------------------------------------------------

    private URLConnection connectionTo(URL url) {
        URLConnection connection = null;

        try {
            connection = url.openConnection();
            connection.connect();
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }

        return connection;
    }
}
