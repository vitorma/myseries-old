package mobi.myseries.test.unit.domain.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;

import org.junit.Test;

public class MergeSeasonWhereEpisodesAreRemoved {

    // Almost all tests in this file verifies whether a SeasonListener has been called.
    // Season#merge should never interact with the listeners, because it would cause a thread
    // which is not the main thread to interact with views and it would break the app. After the
    // merge, these views will be notified that an update has happened and redraw themselves.

    private static Episode mockEpisode(int id, int seriesId, int number, int seasonNumber) {
        Episode episode = Episode.builder()
                .withId(id)
                .withSeriesId(seriesId)
                .withNumber(number)
                .withSeasonNumber(seasonNumber)
                .build();

        return episode;
    }

    private static SeasonListener mockListener() {
        return mock(SeasonListener.class);
    }

    @Test
    public void mergingASeasonWithAnotherRemovesFromItAllEpisodesNotIncludedInTheOther() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        Season season1 = new Season(1, 1).including(episode1);
        Season season2 = new Season(1, 1).including(episode2);

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        assertFalse(season1.mergeWith(season2).includes(episode1));

        verifyZeroInteractions(listener);
    }

    @Test
    public void aSeenSeasonIsKeptSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        episode1.markAsSeen();
        episode2.markAsSeen();
        episode3.markAsSeen();
        episode4.markAsSeen();
        episode5.markAsSeen();

        assertTrue(season1.wasSeen());

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
          // not.including(episode4)
                .including(episode5);

        assertTrue(season1.mergeWith(season2).wasSeen());
        verifyZeroInteractions(listener);
    }

    @Test
    public void itBecomesSeenIfTheRemainingEpisodesAreAllSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        episode1.markAsSeen();
        episode2.markAsSeen();
        episode5.markAsSeen();

        assertFalse(season1.wasSeen());

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
          // not.including(episode3)
          // not.including(episode4)
                .including(episode5);

        assertTrue(season1.mergeWith(season2).wasSeen());
        verifyZeroInteractions(listener);
    }

    @Test
    public void theNextEpisodeToSeeIsChangedIfThePreviousOneIsRemoved() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        episode1.markAsSeen();
        episode2.markAsSeen();
        episode5.markAsSeen();

        assertThat(season1.nextEpisodeToSee(), equalTo(episode3));

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
          // not.including(episode3)
                .including(episode4)
                .including(episode5);

        assertThat(season1.mergeWith(season2).nextEpisodeToSee(), equalTo(episode4));
        verifyZeroInteractions(listener);
    }

    @Test
    public void theNextEpisodeToSeeBecomesNullIfTheSeasonBecomesSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        episode1.markAsSeen();
        episode2.markAsSeen();
        episode5.markAsSeen();

        assertThat(season1.nextEpisodeToSee(), equalTo(episode3));

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
          // not.including(episode3)
          // not.including(episode4)
                .including(episode5);

        assertThat(season1.mergeWith(season2).nextEpisodeToSee(), nullValue());
        verifyZeroInteractions(listener);
    }

    @Test
    public void theNumberOfSeenEpisodesIsFixed() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        episode1.markAsSeen();
        episode2.markAsSeen();
        episode5.markAsSeen();

        assertThat(season1.numberOfSeenEpisodes(), equalTo(3));

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
          // not.including(episode2)
          // not.including(episode3)
                .including(episode4);
          // not.including(episode5);

        assertThat(season1.mergeWith(season2).numberOfSeenEpisodes(), equalTo(1));
        verifyZeroInteractions(listener);
    }

    @Test
    public void theNumberOfEpisodesIsFixed() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        assertThat(season1.numberOfEpisodes(), equalTo(5));

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
          // not.including(episode2)
          // not.including(episode3)
                .including(episode4);
          // not.including(episode5);

        assertThat(season1.mergeWith(season2).numberOfEpisodes(), equalTo(2));
        verifyZeroInteractions(listener);
    }

    @Test
    public void changesInRemovedEpisodesDoesNotAffectTheMergedSeasonAnymore() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);
        Episode episode5 = mockEpisode(5, 1, 5, 1);

        Season season1 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
                .including(episode4)
                .including(episode5);

        episode1.markAsSeen();
        episode2.markAsSeen();
        episode3.markAsSeen();
        episode4.markAsSeen();
        episode5.markAsSeen();

        assertThat(season1.wasSeen(), equalTo(true));

        SeasonListener listener = mockListener();
        assertTrue(season1.register(listener));

        Season season2 = new Season(1, 1)
                .including(episode1)
                .including(episode2)
                .including(episode3)
          // not.including(episode4)
                .including(episode5);

        season1.mergeWith(season2);

        episode4.markAsNotSeen();

        assertThat(season1.wasSeen(), equalTo(true));
        verifyZeroInteractions(listener);
    }
}
