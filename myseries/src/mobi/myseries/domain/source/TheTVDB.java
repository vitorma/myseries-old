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

package mobi.myseries.domain.source;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TheTVDB implements SeriesSource, ImageSource {
    private StreamFactory streamFactory;
    private Set<Integer> fetchedSeriesUpdateMetadata;
    private Map<Integer, String> fetchedPosterUpdateMetadata;

    public TheTVDB(String apiKey) {
        this(new TheTVDBStreamFactory(apiKey));
    }

    public TheTVDB(StreamFactory streamFactory) {
        Validate.isNonNull(streamFactory, "streamFactory");

        this.streamFactory = streamFactory;
    }

    @Override
    public List<Series> searchFor(String seriesName, String languageAbbreviation)
            throws InvalidSearchCriteriaException, ParsingFailedException, ConnectionFailedException, ConnectionTimeoutException {
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
    public Series fetchSeries(int seriesId, String languageAbbreviation)
            throws ParsingFailedException, ConnectionFailedException, SeriesNotFoundException, ConnectionTimeoutException {
        SeriesParser parser = new SeriesParser(this.streamFactory);
        Language language = this.languageFrom(languageAbbreviation);

        try {
            return parser.parse(seriesId, language);
        } catch (StreamCreationFailedException e) {
            throw new SeriesNotFoundException(e);
        }
    }

    @Override
    public List<Series> fetchAllSeries(int[] seriesIds, String languageAbbreviation)
            throws ParsingFailedException, ConnectionFailedException, SeriesNotFoundException, ConnectionTimeoutException {
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

    @Override
    public boolean fetchUpdateMetadataSince(long dateInMiliseconds)
            throws ConnectionFailedException, ParsingFailedException, UpdateMetadataUnavailableException, ConnectionTimeoutException {
        Log.d(this.getClass().getName(), "Fetching update metadata");

        InputStream streamForUpdate = null;

        try {
            streamForUpdate = this.streamFactory.streamForUpdatesSince(dateInMiliseconds);
        } catch (StreamCreationFailedException e) {
            throw new UpdateMetadataUnavailableException(e);
        }

        UpdateParser parser = new UpdateParser(streamForUpdate);

        Log.d(this.getClass().getName(), "Parsing update metadata");

        parser.parse();

        Log.d(this.getClass().getName(), "Update metadata ready");

        this.fetchedSeriesUpdateMetadata = parser.parsedSeries();
        this.fetchedPosterUpdateMetadata = parser.parsedPosters();

        return true;
    }

    private Language languageFrom(String languageAbbreviation) {
        return Language.from(languageAbbreviation, TheTVDBConstants.DEFAULT_LANGUAGE);
    }

    @Override
    public Bitmap fetchSeriesPoster(String filename)
            throws ConnectionFailedException, ImageNotFoundException, ConnectionTimeoutException {
        try {
            return this.bitmapFrom(this.streamFactory.streamForSeriesPoster(filename));
        } catch (StreamCreationFailedException e) {
            throw new ImageNotFoundException(e);
        }
    }

    @Override
    public Bitmap fetchSeriesBanner(String filename)
            throws ConnectionFailedException, ImageNotFoundException, ConnectionTimeoutException {
        try {
            //TODO Create a specific method to SeriesBanner
            return this.bitmapFrom(this.streamFactory.streamForSeriesPoster(filename));
        } catch (StreamCreationFailedException e) {
            throw new ImageNotFoundException(e);
        }
    }

    @Override
    public Bitmap fetchEpisodeImage(String filename)
            throws ConnectionFailedException, ImageNotFoundException, ConnectionTimeoutException {
        try {
            return this.bitmapFrom(this.streamFactory.streamForEpisodeImage(filename));
        } catch (StreamCreationFailedException e) {
            throw new ImageNotFoundException(e);
        }
    }

    private Bitmap bitmapFrom(InputStream inputStream) {
        return BitmapFactory.decodeStream(inputStream);
    }

    @Override
    public Collection<Integer> seriesUpdateMetadata() {
        return this.fetchedSeriesUpdateMetadata;
    }

    @Override
    public Map<Integer, String> posterUpdateMetadata() {
        return this.fetchedPosterUpdateMetadata;
    }
}
