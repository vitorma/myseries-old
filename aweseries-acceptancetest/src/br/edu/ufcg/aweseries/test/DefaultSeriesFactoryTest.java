package br.edu.ufcg.aweseries.test;

import br.edu.ufcg.aweseries.model.Series;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultSeriesFactoryTest extends TestCase {

    private DefaultSeriesFactory factory;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        this.factory = new DefaultSeriesFactory();
    }

    public void testNonExistentAttributeKeyThrowsIllegalArgumentException() {
        try {
            this.factory.createSeries("game : Sudoku");
            fail("Should have thrown an exception for nonexistent attribute key");
        } catch (IllegalArgumentException e) {}
    }

    public void testNotParameterizedCreationReturnsDefaultData() {
        Series defaultSeries = this.factory.createSeries();

        // Series ID should be randomized - assertThat(defaultSeries.getId(), equalTo("0"));
        assertThat(defaultSeries.getName(), equalTo("Default Series"));
        assertThat(defaultSeries.getStatus(), equalTo("Continuing"));
        assertThat(defaultSeries.getAirsDay(), equalTo("Monday"));
        assertThat(defaultSeries.getAirsTime(), equalTo("8:00 PM"));
        assertThat(defaultSeries.getFirstAired(), equalTo("1996-01-01"));
        assertThat(defaultSeries.getRuntime(), equalTo("60"));
        assertThat(defaultSeries.getNetwork(), equalTo("BBC"));
        assertThat(defaultSeries.getOverview(), equalTo("A default series that has been created"));
        assertThat(defaultSeries.getGenres(), equalTo("Action"));
        assertThat(defaultSeries.getActors(), equalTo("Wile E. Coyote, Road Runner"));
        assertThat(defaultSeries.getPoster(), nullValue());
    }

    public void testSeriesIdIsRandomizedSoDifferentSeriesAreCreatedEachTime() {
        Series firstSeries = this.factory.createSeries();
        Series secondSeries = this.factory.createSeries();

        assertThat(firstSeries, not(equalTo(secondSeries)));
        assertThat(firstSeries.getId(), not(equalTo(secondSeries.getId())));
    }

    public void testWhenEqualSeriesIdsAreChoosenThenEqualSeriesAreCreated() {
        Series firstSeries = this.factory.createSeries("id : 0");
        Series secondSeries = this.factory.createSeries("id : 0");

        assertThat(firstSeries, equalTo(secondSeries));
        assertThat(firstSeries.getId(), equalTo(secondSeries.getId()));
    }

    public void testDefaultSeriesWithCustomId() {
        Series defaultSeries = this.factory.createSeries("id : 123");

        assertThat(defaultSeries.getId(), equalTo("123"));
    }

    public void testDefaultSeriesWithCustomIdAndName() {
        Series defaultSeries = this.factory.createSeries("id : 123", "name : Series Name");

        assertThat(defaultSeries.getId(), equalTo("123"));
        assertThat(defaultSeries.getName(), equalTo("Series Name"));
    }

    public void testAllParameters() {
        String id = "123";
        String name = "Not Default Series";
        String status = "Ended";
        String airsOn = "";
        String airsAt = "";
        String firstAired = "1997-12-21";
        String runtime = "30";
        String network = "ABC";
        String overview = "A not default series to be used in this test.";
        String genres = "Drama";
        String actors = "Who Is He, Who Is She";

        Series defaultSeries = this.factory.createSeries("id : " + id,
                                                         "name : " + name,
                                                         "status : " + status,
                                                         "airsOn : " + airsOn,
                                                         "airsAt : " + airsAt,
                                                         "firstAired : " + firstAired,
                                                         "runtime : " + runtime,
                                                         "network : " + network,
                                                         "overview : " + overview,
                                                         "genres : " + genres,
                                                         "actors : " + actors);

        assertThat(defaultSeries.getId(), equalTo(id));
        assertThat(defaultSeries.getName(), equalTo(name));
        assertThat(defaultSeries.getStatus(), equalTo(status));
        assertThat(defaultSeries.getAirsDay(), equalTo(airsOn));
        assertThat(defaultSeries.getAirsTime(), equalTo(airsAt));
        assertThat(defaultSeries.getFirstAired(), equalTo(firstAired));
        assertThat(defaultSeries.getRuntime(), equalTo(runtime));
        assertThat(defaultSeries.getNetwork(), equalTo(network));
        assertThat(defaultSeries.getOverview(), equalTo(overview));
        assertThat(defaultSeries.getGenres(), equalTo(genres));
        assertThat(defaultSeries.getActors(), equalTo(actors));
        assertThat(defaultSeries.getPoster(), nullValue());
    }
}
