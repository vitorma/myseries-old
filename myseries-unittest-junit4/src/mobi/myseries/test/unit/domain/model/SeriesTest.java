package mobi.myseries.test.unit.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Airtime;

import org.junit.Test;

public class SeriesTest {
    private static final int ID = 1;
    private static final String ACTORS = "actors";
    private static final Date AIR_DATE = new Date(1);
    private static final String AIR_DAY = "air day";
    private static final Airtime AIR_TIME = Airtime.valueOf(1);
    private static final String GENRES = "genres";
    private static final String NETWORK = "network";
    private static final String NAME = "Series";
    private static final String OVERVIEW = "overview";
    private static final String POSTER_FILENAME = "poster filename";
    private static final String RUNTIME = "runtime";
    private static final String STATUS = "status";

    /* Merge */

    @Test
    public void mergeShouldUpdateProperties() {
        Series series = incompleteSeries();
        Series seriesUpdate = completeSeries();

        series.mergeWith(seriesUpdate);

        assertEquals(NAME, series.name());
        assertEquals(ACTORS, series.actors());
        assertEquals(AIR_DATE, series.airDate());
        assertEquals(AIR_DAY, series.airDay());
        assertEquals(AIR_TIME, series.airtime());
        assertEquals(GENRES, series.genres());
        assertEquals(ID, series.id());
        assertEquals(NETWORK, series.network());
        assertEquals(NAME, series.name());
        assertEquals(OVERVIEW, series.overview());
        assertEquals(POSTER_FILENAME, series.posterFileName());
        assertEquals(RUNTIME, series.runtime());
        assertEquals(STATUS, series.status());
    }

    @Test
    public void mergeShouldAddEpisodes() {
        Series series = incompleteSeries();
        Series seriesUpdate = completeSeries();

        List<Episode> episodes = episodesList(2, 10);
        series = series.includingAll(episodes);

        for (int i = 11; i <= 20; ++i) {
            episodes.add(episodeMock(i, 2, ID));
        }

        seriesUpdate = seriesUpdate.includingAll(episodes);

        assertEquals(20, series.episodes().size());
        assertEquals(20, series.numberOfEpisodes());
        assertEquals(30, seriesUpdate.episodes().size());
        assertEquals(30, seriesUpdate.numberOfEpisodes());

        series.mergeWith(seriesUpdate);

        assertEquals(30, series.episodes().size());
        assertEquals(30, series.numberOfEpisodes());

        for (Episode e : episodes) {
            assertTrue(series.episodes().contains(e));
            assertTrue(seriesUpdate.episodes().contains(e));
        }
    }

    /* Auxiliary */

    private static Episode episodeMock(int number, int seasonNumber, int seriesId) {
        Episode episode = mock(Episode.class);
        doReturn(100 * seasonNumber + number).when(episode).id();
        doReturn(number).when(episode).number();
        doReturn(seriesId).when(episode).seriesId();
        doReturn(seasonNumber).when(episode).seasonNumber();

        return episode;
    }

    private static Series incompleteSeries() {
        return Series.builder()
        .withName("Seri")
        .withId(ID)
        .build();
    }

    private static Series completeSeries() {
        return Series.builder()
        .withName(NAME)
        .withId(ID)
        .withActors(ACTORS)
        .withAirDate(AIR_DATE)
        .withAirDay(AIR_DAY)
        .withAirtime(AIR_TIME)
        .withGenres(GENRES)
        .withNetwork(NETWORK)
        .withOverview(OVERVIEW)
        .withPosterFileName(POSTER_FILENAME)
        .withRuntime(RUNTIME)
        .withStatus(STATUS)
        .build();
    }

    private static List<Episode> episodesList(int numberOfSeasons, int episodesPerSeason) {
        List<Episode> episodes = new ArrayList<Episode>();
        for (int season = 1; season <= numberOfSeasons; ++season) {
            for (int episode = 1; episode <= episodesPerSeason; ++episode) {
                episodes.add(episodeMock(episode, season, ID));
            }
        }

        return episodes;
    }
}
