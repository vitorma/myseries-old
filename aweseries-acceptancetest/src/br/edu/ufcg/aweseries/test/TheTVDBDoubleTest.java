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

    public void testCreateSeriesWithName() {
        this.theTVDB.createSeries(Language.EN, "name : Given Name");

        Collection<Series> results = this.theTVDB.searchFor("Given", Language.EN);

        assertThat(results, hasItem(namedAs("Given Name")));
    }

    // Localization
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
}
