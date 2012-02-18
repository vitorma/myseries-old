/*
 *   TheTVDBStreamFactoryTest.java
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

package br.edu.ufcg.aweseries.test.unit.series_source;

import org.junit.Test;

import br.edu.ufcg.aweseries.series_source.Language;
import br.edu.ufcg.aweseries.series_source.TheTVDBStreamFactory;

public class TheTVDBStreamFactoryTest {
    private static final String BLANK_STRING = "  \n         \t  \n ";
    private static final String API_KEY = "AK1";
    private static final String NON_BLANK_STRING = "a";

    //Construction------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void constructingATheTVDBStreamFactoryWithNullApiKeyCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingATheTVDBStreamFactoryWithBlankApiKeyCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(BLANK_STRING);
    }

    //Series------------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForSeriesWithNullLanguageCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForSeries(1, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForSeriesSearchWithNullNameCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForSeriesSearch(null, Language.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForSeriesSearchWithBlankNameCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForSeriesSearch(BLANK_STRING, Language.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForSeriesSearchWithNullLanguageCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForSeriesSearch(NON_BLANK_STRING, null);
    }

    //Image-------------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForSeriesPosterWithNullFileNameCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForSeriesPoster(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForSeriesPosterWithBlankFileNameCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForSeriesPoster(BLANK_STRING);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForEpisodeImageWithNullFileNameCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForEpisodeImage(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingStreamForEpisodeImageWithBlankFileNameCausesIllegalArgumentException() {
        new TheTVDBStreamFactory(API_KEY).streamForEpisodeImage(BLANK_STRING);
    }
}
