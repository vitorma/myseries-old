package mobi.myseries.test.unit.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.WeekDay;

import org.junit.Test;
import org.mockito.Mockito;

public class SeriesTest {
    private static final int ID = 1;
    private static final String ACTORS = "actors";
    private static final Date AIR_DATE = new Date(1);
    private static final WeekDay AIR_DAY = WeekDay.valueOf(1);
    private static final Time AIR_TIME = Time.valueOf(1);
    private static final String GENRES = "genres";
    private static final String NETWORK = "network";
    private static final String NAME = "Series";
    private static final String OVERVIEW = "overview";
    private static final String POSTER_FILENAME = "poster filename";
    private static final String RUNTIME = "runtime";
    private static final Status STATUS = Status.from("status");

    /* Merge */

    @Test
    public void mergeShouldUpdateProperties() {
        Series series = incompleteSeries();
        Series seriesUpdate = completeSeries();

        series.mergeWith(seriesUpdate);

        assertEquals(seriesUpdate.name(), series.name());
        assertEquals(seriesUpdate.actors(), series.actors());
        assertEquals(seriesUpdate.airDate(), series.airDate());
        assertEquals(seriesUpdate.airDay(), series.airDay());
        assertEquals(seriesUpdate.airtime(), series.airtime());
        assertEquals(seriesUpdate.genres(), series.genres());
        assertEquals(seriesUpdate.id(), series.id());
        assertEquals(seriesUpdate.network(), series.network());
        assertEquals(seriesUpdate.name(), series.name());
        assertEquals(seriesUpdate.overview(), series.overview());
        assertEquals(seriesUpdate.posterUrl(), series.posterUrl());
        assertEquals(seriesUpdate.runtime(), series.runtime());
        assertEquals(seriesUpdate.status(), series.status());
    }

    @Test
    public void mergeShouldAddEpisodes() {
        Series outdatedSeries = incompleteSeries();
        Series updatedSeries = completeSeries();

        List<Episode> outdatedEpisodes = episodesList(2, 10, outdatedSeries);
        List<Episode> updatedEpisodes = episodesList(3, 10, updatedSeries);

        outdatedSeries = outdatedSeries.includingAll(outdatedEpisodes);
        updatedSeries = updatedSeries.includingAll(updatedEpisodes);

        assertEquals(20, outdatedSeries.episodes().size());
        assertEquals(20, outdatedSeries.numberOfEpisodes());
        assertEquals(30, updatedSeries.episodes().size());
        assertEquals(30, updatedSeries.numberOfEpisodes());

        outdatedSeries.mergeWith(updatedSeries);

        assertEquals(30, outdatedSeries.episodes().size());
        assertEquals(30, outdatedSeries.numberOfEpisodes());
        assertEquals(30, updatedSeries.episodes().size());
        assertEquals(30, updatedSeries.numberOfEpisodes());

        for (Episode e : updatedEpisodes) {
            assertTrue(includes(outdatedSeries, e));
            assertTrue(includes(updatedSeries, e));
        }
    }

    @Test
    public void mergeShouldRemoveEpisodes() {
        Series outdatedSeries = incompleteSeries();
        Series updatedSeries = completeSeries();

        List<Episode> outdatedEpisodes = episodesList(2, 10, outdatedSeries);
        List<Episode> updatedEpisodes = episodesList(3, 6, updatedSeries);

        outdatedSeries = outdatedSeries.includingAll(outdatedEpisodes);
        updatedSeries = updatedSeries.includingAll(updatedEpisodes);

        assertEquals(20, outdatedSeries.episodes().size());
        assertEquals(20, outdatedSeries.numberOfEpisodes());
        assertEquals(18, updatedSeries.episodes().size());
        assertEquals(18, updatedSeries.numberOfEpisodes());

        outdatedSeries.mergeWith(updatedSeries);

        assertEquals(18, outdatedSeries.episodes().size());
        assertEquals(18, outdatedSeries.numberOfEpisodes());
        assertEquals(18, updatedSeries.episodes().size());
        assertEquals(18, updatedSeries.numberOfEpisodes());

        for (Episode e : updatedEpisodes) {
            assertTrue(includes(outdatedSeries, e));
            assertTrue(includes(updatedSeries, e));
        }
    }

    @Test
    public void mergeShouldRemoveSeasons() {
        Series outdatedSeries = incompleteSeries();
        Series updatedSeries = completeSeries();

        List<Episode> outdatedEpisodes = episodesList(3, 10, outdatedSeries);
        List<Episode> updatedEpisodes = episodesList(2, 6, updatedSeries);

        outdatedSeries = outdatedSeries.includingAll(outdatedEpisodes);
        updatedSeries = updatedSeries.includingAll(updatedEpisodes);

        assertEquals(3, outdatedSeries.seasons().numberOfSeasons());
        assertEquals(2, updatedSeries.seasons().numberOfSeasons());

        outdatedSeries.mergeWith(updatedSeries);

        assertEquals(2, outdatedSeries.seasons().numberOfSeasons());
        assertEquals(2, updatedSeries.seasons().numberOfSeasons());

        for (Episode e : updatedEpisodes) {
            assertTrue(includes(outdatedSeries, e));
            assertTrue(includes(updatedSeries, e));
        }
    }

    /* Auxiliary */

    private static Series incompleteSeries() {
        return Series.builder()
                     .withTitle("Seri")
                     .withTvdbId(ID)
                     .build();
    }

    private static Series completeSeries() {
        return Series.builder()
                     .withTitle(NAME)
                     .withTvdbId(ID)
                     .withActors(ACTORS)
                     .withAirDate(AIR_DATE)
                     .withAirDay(AIR_DAY)
                     .withAirTime(AIR_TIME)
                     .withGenres(GENRES)
                     .withNetwork(NETWORK)
                     .withOverview(OVERVIEW)
                     .withPoster(POSTER_FILENAME)
                     .withRuntime(RUNTIME)
                     .withStatus(STATUS)
                     .build();
    }

    private static boolean includes(Series series, Episode episode) {
        for (Episode e : series.episodes()) {
            if (e.id() == episode.id()) {
                return true;
            }
        }

        return false;
    }

    private static List<Episode> episodesList(int numberOfSeasons, int episodesPerSeason, Series series) {
        List<Episode> episodes = new ArrayList<Episode>();

        for (int season = 1; season <= numberOfSeasons; ++season) {
            for (int episode = 1; episode <= episodesPerSeason; ++episode) {
                episodes.add(episodeMock(episode, season, series));
            }
        }

        return episodes;
    }

    private static Episode episodeMock(int number, int seasonNumber, Series series) {
        Episode episode = mock(Episode.class);

        Mockito.when(episode.id()).thenReturn(100 * seasonNumber + number);
        Mockito.when(episode.seriesId()).thenReturn(series.id());
        Mockito.when(episode.number()).thenReturn(number);
        Mockito.when(episode.seasonNumber()).thenReturn(seasonNumber);
        Mockito.when(episode.withAirtime(series.airtime())).thenReturn(episode);

        return episode;
    }
}
