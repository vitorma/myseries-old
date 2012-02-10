/*
 *   UrlFactoryTest.java
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

package br.edu.ufcg.aweseries.test.unit.thetvdb;

import org.junit.Test;

import br.edu.ufcg.aweseries.series_source.Language;
import br.edu.ufcg.aweseries.series_source.UrlFactory;

public class UrlFactoryTest {
    private static final String API_KEY = "AK1";
    private static final String BLANK_STRING = "  \n         \t  \n ";
    private static final String NON_BLANK_STRING = "a";

    //Construction------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void constructingAnUrlFactoryWithNullApiKeyCausesIllegalArgumentException() {
        new UrlFactory(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingAnUrlFactoryWithBlankApiKeyCausesIllegalArgumentException() {
        new UrlFactory(BLANK_STRING);
    }

    //Series------------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesWithNullLanguageCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeries(1, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesSearchWithNullNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesSearch(null, Language.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesSearchWithBlankNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesSearch(BLANK_STRING, Language.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesSearchWithNullLanguageCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesSearch(NON_BLANK_STRING, null);
    }

    //Image-------------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesPosterWithNullFileNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesPoster(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesPosterWithBlankFileNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesPoster(BLANK_STRING);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForEpisodeImageWithNullFileNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForEpisodeImage(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForEpisodeImageWithBlankFileNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForEpisodeImage(BLANK_STRING);
    }
}
