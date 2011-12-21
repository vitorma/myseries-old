package br.edu.ufcg.aweseries.test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static br.edu.ufcg.aweseries.test.matchers.SeriesMatchers.*;

import br.edu.ufcg.aweseries.model.Series;

import junit.framework.TestCase;

public class TheTVDBDoubleTest extends TestCase {

    private static final String LANGUAGE_EN = "en";
    private static final String LANGUAGE_PT = "pt";
    private static final String LANGUAGE_ES = "es";
    private static final String UNAVAILABLE_LANGUAGE = "zz";

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
        this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name");

        // Fetch
        Series fetched = this.theTVDB.fetchSeries("123", LANGUAGE_EN);
        assertThat(fetched, hasId("123"));
        assertThat(fetched, namedAs("Given Name"));

        // Search
        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);
        assertThat(results, hasItem(namedAs("Given Name")));
    }

    public void testCreateSeriesWithNullLanguageThrowsException() {
        try {
            this.theTVDB.createSeries(null);
            fail("Should have thrown an IllegalArgumentException for null language");
        } catch (IllegalArgumentException e) {}
    }

    public void testCreateSeriesWithUnavailableLanguageThrowsException() {
        try {
            this.theTVDB.createSeries(UNAVAILABLE_LANGUAGE);
            fail("Should have thrown an IllegalArgumentException for unavailable language");
        } catch (IllegalArgumentException e) {}
    }

    public void testCreateSeriesWithNullAttributesThrowsException() {
        try {
            this.theTVDB.createSeries(LANGUAGE_EN, (String) null);
            fail("Should have thrown an IllegalArgumentException for null attribute");
        } catch (IllegalArgumentException e) {}
    }

    // Search
    public void testWhenNoResultsAreFoundReturnsAnEmptyCollection() {
        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);

        assertTrue(results.isEmpty());
    }

    public void testEnglishUsualSearchReturnsTheSameResultOnlyOnce() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");

        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);

        assertThat(results, hasItem(namedAs("Given Name")));
        assertThat(results.size(), equalTo(1));
    }

    public void testNotSimilarResultsAreNotReturned() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Another Series");

        Collection<Series> results = this.theTVDB.searchFor("Another", LANGUAGE_EN);

        assertThat(results, hasItem(namedAs("Another Series")));
        assertThat(results, not(hasItem(namedAs("Given Name"))));
    }

    public void testWhatShouldHappenInAUsualLocalizedSearch() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Another Series");
        this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");
        this.theTVDB.createSeries(LANGUAGE_ES, "name : Given lo Malo");

        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

        assertThat(results, hasItem(namedAs("Given Name")));
        assertThat(results, hasItem(namedAs("Cavalo Given nao se Olha os Dentes")));
        assertThat(results, not(hasItem(namedAs("Another Series"))));
        assertThat(results, not(hasItem(namedAs("Given lo Malo"))));
    }

    // Search Localization
    public void testLocalizedSearchReturnsLocalizedResults() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
        this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");

        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

        assertThat(results, hasItem(namedAs("Cavalo Given nao se Olha os Dentes")));
    }

    public void testLocalizedSearchReturnsOnlyLocalizedResultsForTheGivenLocale() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
        this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");
        this.theTVDB.createSeries(LANGUAGE_ES, "name : Given lo Malo");

        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

        assertThat(results, not(hasItem(namedAs("Given lo Malo"))));
    }

    public void testEnglishSearchOnlyReturnsResultsInEnglish() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
        this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");

        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);

        assertThat(results, not(hasItem(namedAs("Cavalo Given nao se Olha os Dentes"))));
    }

    public void testLocalizedSearchAlsoReturnsResultsInEnglish() {
        this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
        this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");

        Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

        assertThat(results, hasItem(namedAs("Given Name")));
    }

    // Search Arguments Validation
    public void testSearchForNullNameThrowsException() {
        try {
            this.theTVDB.searchFor(null, LANGUAGE_EN);
            fail("Should have thrown an IllegalArgumentException for searching for null name");
        } catch (IllegalArgumentException e) {}
    }

    public void testSearchForNullLanguageThrowsException() {
        try {
            this.theTVDB.searchFor("Series Name", null);
            fail("Should have thrown an IllegalArgumentException for searching for null language");
        } catch (IllegalArgumentException e) {}
    }
    
    public void testSearchForUnavailableLanguageThrowsException() {
        try {
            this.theTVDB.searchFor("Series Name", UNAVAILABLE_LANGUAGE);
            fail("Should have thrown an IllegalArgumentException for unavailable language");
        } catch (IllegalArgumentException e) {}
    }

    // Fetch Series
    public void testFetchNonExistentSeries() {
        assertThat(this.theTVDB.fetchSeries("123", LANGUAGE_EN), nullValue());
    }

    public void testFetchExistentSeriesInEnglish() {
        this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");

        Series fetched = this.theTVDB.fetchSeries("123", LANGUAGE_EN);

        assertThat(fetched, hasId("123"));
        assertThat(fetched, namedAs("Given Name"));
        assertThat(fetched, overviewedAs("An example of series"));
    }

    public void testFetchExistentSeriesFromDifferentLocale() {
        this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
        this.theTVDB.createSeries(LANGUAGE_PT, "id : 123", "name : Given Name", "overview : Um exemplo de serie");

        Series fetched = this.theTVDB.fetchSeries("123", LANGUAGE_PT);

        assertThat(fetched, hasId("123"));
        assertThat(fetched, namedAs("Given Name"));
        assertThat(fetched, overviewedAs("Um exemplo de serie"));
    }

    public void testFetchExistentSeriesFromLocaleWhereItDoesNotExistReturnsEnglishVersion() {
        this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
        this.theTVDB.createSeries(LANGUAGE_PT, "id : 123", "name : Given Name", "overview : Um exemplo de serie");

        Series fetched = this.theTVDB.fetchSeries("123", LANGUAGE_ES);

        assertThat(fetched, hasId("123"));
        assertThat(fetched, namedAs("Given Name"));
        assertThat(fetched, overviewedAs("An example of series"));
    }

    // Fetch Series Arguments Validation
    public void testFetchNullSeriesIdThrowsException() {
        try {
            this.theTVDB.fetchSeries(null, LANGUAGE_EN);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testFetchNullLanguageThrowsException() {
        try {
            this.theTVDB.fetchSeries("123", null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testFetchUnavailableLanguageThrowsException() {
        try {
            this.theTVDB.fetchSeries("123", UNAVAILABLE_LANGUAGE);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // TODO
    // Deal with Episodes
}
