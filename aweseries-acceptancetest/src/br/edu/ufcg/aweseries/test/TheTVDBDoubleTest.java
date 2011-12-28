/*
 *   TheTVDBDoubleTest.java
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

package br.edu.ufcg.aweseries.test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static br.edu.ufcg.aweseries.test.SeriesMatchers.*;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.Language;

import junit.framework.TestCase;

public class TheTVDBDoubleTest extends TestCase {

    private TheTVDBDouble theTVDB;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // our code
        this.theTVDB = new TheTVDBDouble();
    }

    @Override
    public void tearDown() throws Exception {
        // our code
        super.tearDown();
    }

    // Create Series
    public void testCreateSeriesWithName() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.EN);

        assertThat(results, hasItem(namedAs("Given Name")));
    }

    public void testCreateSeriesWithNullLanguageThrowsIllegalArgumentException() {
        try {
            this.theTVDB.createSeries(null);
            fail("Should have thrown an IllegalArgumentException for null language");
        } catch (IllegalArgumentException e) {}
    }

    public void testCreateSeriesWithNullAttributesThrowsIllegalArgumentException() {
        try {
            this.theTVDB.createSeries(Language.EN, (String) null);
            fail("Should have thrown an IllegalArgumentException for null attribute");
        } catch (IllegalArgumentException e) {}
    }

    // Search
    public void testNotSimilarResultsAreNotReturned() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");
        this.theTVDB.createSeries(Language.EN, "name : Another Series");

        Collection<Series> results = this.theTVDB.searchFor("Another", Language.EN);

        assertThat(results, hasItem(namedAs("Another Series")));
        assertThat(results, not(hasItem(namedAs("Given Name"))));
    }

    public void testWhatShouldHappenInAUsualLocalizedSearch() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");
        this.theTVDB.createSeries(Language.EN, "name : Another Series");
        this.theTVDB.createSeries(Language.PT, "name : Cavalo Given nao se Olha os Dentes");
        this.theTVDB.createSeries(Language.ES, "name : Given lo Malo");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.PT);

        assertThat(results, hasItem(namedAs("Given Name")));
        assertThat(results, hasItem(namedAs("Cavalo Given nao se Olha os Dentes")));
        assertThat(results, not(hasItem(namedAs("Another Series"))));
        assertThat(results, not(hasItem(namedAs("Given lo Malo"))));
    }

    // Search Localization
    public void testLocalizedSearchReturnsLocalizedResults() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");
        this.theTVDB.createSeries(Language.PT, "name : Cavalo Given nao se Olha os Dentes");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.PT);

        assertThat(results, hasItem(namedAs("Cavalo Given nao se Olha os Dentes")));
    }

    public void testLocalizedSearchReturnsOnlyLocalizedResultsForTheGivenLocale() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");
        this.theTVDB.createSeries(Language.PT, "name : Cavalo Given nao se Olha os Dentes");
        this.theTVDB.createSeries(Language.ES, "name : Given lo Malo");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.PT);

        assertThat(results, not(hasItem(namedAs("Given lo Malo"))));
    }

    public void testEnglishSearchOnlyReturnsResultsInEnglish() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");
        this.theTVDB.createSeries(Language.PT, "name : Cavalo Given nao se Olha os Dentes");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.EN);

        assertThat(results, not(hasItem(namedAs("Cavalo Given nao se Olha os Dentes"))));
    }

    public void testLocalizedSearchAlsoReturnsResultsInEnglish() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");
        this.theTVDB.createSeries(Language.PT, "name : Cavalo Given nao se Olha os Dentes");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.PT);

        assertThat(results, hasItem(namedAs("Given Name")));
    }

    // Search Arguments Validation
    public void testSearchForNullNameThrowsIllegalArgumentException() {
        try {
            this.theTVDB.searchFor(null, Language.EN);
            fail("Should have thrown an IllegalArgumentException for searching for null name");
        } catch (IllegalArgumentException e) {}
    }

    public void testSearchForNullLanguageThrowsIllegalArgumentException() {
        try {
            this.theTVDB.searchFor("Series Name", null);
            fail("Should have thrown an IllegalArgumentException for searching for null language");
        } catch (IllegalArgumentException e) {}
    }
}
