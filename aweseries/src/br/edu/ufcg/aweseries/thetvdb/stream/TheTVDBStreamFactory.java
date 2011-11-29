/*
 *   TheTVDBStreamFactory.java
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

package br.edu.ufcg.aweseries.thetvdb.stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import br.edu.ufcg.aweseries.thetvdb.stream.url.UrlSupplier;
import br.edu.ufcg.aweseries.util.Strings;

public class TheTVDBStreamFactory implements StreamFactory {
    private UrlSupplier urlSupplier;

    public TheTVDBStreamFactory(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey should not be null");
        }

        this.urlSupplier = new UrlSupplier(apiKey);
    }

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        String baseSeriesUrl = this.urlSupplier.getBaseSeriesUrl(seriesId);
        return buffered(streamFor(baseSeriesUrl));
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        String fullSeriesUrl = this.urlSupplier.getFullSeriesUrl(seriesId);
        return buffered(streamFor(fullSeriesUrl));
    }

    @Override
    public InputStream streamForFullSeries(String seriesId, String language) {
        String fullSeriesUrl = this.urlSupplier.getFullSeriesUrl(seriesId, language);
        return buffered(streamFor(fullSeriesUrl));
    }

    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        String seriesPosterUrl = this.urlSupplier.getSeriesPosterUrl(resourcePath);
        return streamFor(seriesPosterUrl);
    }

    @Override
    public InputStream streamForSeriesSearch(String seriesName) {
        this.checkIfItIsAValidUrlSuffix(seriesName, "seriesName");

        String seriesSearchUrl = this.urlSupplier.getSeriesSearchUrl(seriesName);
        return buffered(streamFor(seriesSearchUrl));
    }

    @Override
    public InputStream streamForSeriesSearch(String seriesName, String language) {
        String seriesSearchUrl = this.urlSupplier.getSeriesSearchUrl(seriesName, language);
        return buffered(streamFor(seriesSearchUrl));
    }

    private void checkIfItIsAValidUrlSuffix(String suffix, String parameterName) {
        if (suffix == null) {
            throw new IllegalArgumentException(parameterName + " should not be null");
        }
        if (Strings.isBlank(suffix)) {
            throw new IllegalArgumentException(parameterName + " should not be blank");
        }
    }

    private BufferedInputStream buffered(InputStream stream) {
        return new BufferedInputStream(stream);
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
}
