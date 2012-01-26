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

import br.edu.ufcg.aweseries.thetvdb.Language;
import br.edu.ufcg.aweseries.thetvdb.stream.url.UrlFactory;

public class UrlFactoryTest {
    private static final String API_KEY = "AK1";
    private static final String BLANK_STRING = "    \t \n ";

    //Construction------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void constructingAnUrlSupplierWithNullApiKeyCausesIllegalArgumentException() {
        new UrlFactory(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingAnUrlSupplierWithBlankApiKeyCausesIllegalArgumentException() {
        new UrlFactory(BLANK_STRING);
    }

    //Series------------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesWithNullLanguageCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeries(1, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesSearchWithNullNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesSearch(null, Language.EN);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesSearchWithBlankNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesSearch(BLANK_STRING, Language.EN);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForSeriesSearchWithNullLanguageCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesSearch("a", null);
    }

    //Image-------------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForPosterWithNullFileNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesPoster(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingAnUrlForPosterWithBlankFileNameCausesIllegalArgumentException() {
        new UrlFactory(API_KEY).urlForSeriesPoster(BLANK_STRING);
    }
}
