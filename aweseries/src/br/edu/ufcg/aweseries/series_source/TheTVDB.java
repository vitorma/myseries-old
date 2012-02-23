/*
 *   TheTVDB.java
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.ImageSource;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Validate;

public class TheTVDB implements SeriesSource, ImageSource {
    private StreamFactory streamFactory;

    //Construction------------------------------------------------------------------------------------------------------

    public TheTVDB(String apiKey) {
        this(new TheTVDBStreamFactory(apiKey));
    }

    public TheTVDB(StreamFactory streamFactory) {
        Validate.isNonNull(streamFactory, "streamFactory");

        this.streamFactory = streamFactory;
    }

    //SeriesSource------------------------------------------------------------------------------------------------------

    @Override
    public List<Series> searchFor(String seriesName, String languageAbbreviation) {
        Validate.isNonBlank(seriesName, new InvalidSearchCriteriaException());

        SeriesSearchParser parser = new SeriesSearchParser(this.streamFactory);
        Language language = this.languageFrom(languageAbbreviation);

        try {
            return parser.parse(seriesName, language);
        } catch (StreamCreationFailedException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Series fetchSeries(int seriesId, String languageAbbreviation) {
        SeriesParser parser = new SeriesParser(this.streamFactory);
        Language language = this.languageFrom(languageAbbreviation);

        try {
            return parser.parse(seriesId, language);
        } catch (StreamCreationFailedException e) {
            throw new SeriesNotFoundException(e);
        }
    }

    @Override
    public List<Series> fetchAllSeries(int[] seriesIds, String languageAbbreviation) {
        Validate.isNonNull(seriesIds, "seriesIds");

        SeriesParser parser = new SeriesParser(this.streamFactory);
        Language language = this.languageFrom(languageAbbreviation);

        List<Series> result = new ArrayList<Series>();

        for (int seriesId : seriesIds) {
            try {
                result.add(parser.parse(seriesId, language));
            } catch (StreamCreationFailedException e) {
                throw new SeriesNotFoundException(e);
            }
        }

        return result;
    }

    //Language----------------------------------------------------------------------------------------------------------

    private Language languageFrom(String languageAbbreviation) {
        return Language.from(languageAbbreviation, TheTvDbConstants.DEFAULT_LANGUAGE);
    }

    @Override
    public Bitmap fetchSeriesPoster(String filename) {
        InputStream is = this.streamFactory.streamForSeriesPoster(filename);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        return bmp;
    }

    @Override
    public Bitmap fetchEpisodeImage(String filename) {
        InputStream is = this.streamFactory.streamForEpisodeImage(filename);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        return bmp;
    }
}
