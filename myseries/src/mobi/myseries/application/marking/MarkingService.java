package mobi.myseries.application.marking;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;

public class MarkingService extends ApplicationService<MarkingListener> {

    public MarkingService(Environment environment) {
        super(environment);
    }

    /* Interface */

    public void markAsWatched(Episode episodeToMark) {
        Validate.isNonNull(episodeToMark, "episodeToMark");

        episodeToMark.markAsWatched();
        environment().seriesRepository().update(episodeToMark);

        notifyMarking(episodeToMark);
    }

    public void markAsWatched(Season seasonToMark) {
        Validate.isNonNull(seasonToMark, "seasonToMark");

        seasonToMark.markAsSeen();
        environment().seriesRepository().updateAllEpisodes(seasonToMark.episodes());

        notifyMarking(seasonToMark);
    }

    public void markAsWatched(Series seriesToMark) {
        Validate.isNonNull(seriesToMark, "seriesToMark");

        seriesToMark.markAsSeen();
        environment().seriesRepository().update(seriesToMark);

        notifyMarking(seriesToMark);
    }

    public void markAsUnwatched(Episode episodeToMark) {
        Validate.isNonNull(episodeToMark, "episodeToMark");

        episodeToMark.markAsUnwatched();
        environment().seriesRepository().update(episodeToMark);

        notifyMarking(episodeToMark);
    }

    public void markAsUnwatched(Season seasonToMark) {
        Validate.isNonNull(seasonToMark, "seasonToMark");

        seasonToMark.markAsNotSeen();
        environment().seriesRepository().updateAllEpisodes(seasonToMark.episodes());

        notifyMarking(seasonToMark);
    }

    public void markAsUnwatched(Series seriesToMark) {
        Validate.isNonNull(seriesToMark, "seriesToMark");

        seriesToMark.markAsNotSeen();
        environment().seriesRepository().update(seriesToMark);

        notifyMarking(seriesToMark);
    }

    /* Notifications */

    private void notifyMarking(final Episode markedEpisode) {
        for (MarkingListener listener: listeners()) {
            listener.onMarked(markedEpisode);
        }

        broadcast(BroadcastAction.MARKING);
    }

    private void notifyMarking(final Season markedSeason) {
        for (MarkingListener listener: listeners()) {
            listener.onMarked(markedSeason);
        }

        broadcast(BroadcastAction.MARKING);
    }

    private void notifyMarking(final Series markedSeries) {
        for (MarkingListener listener: listeners()) {
            listener.onMarked(markedSeries);
        }

        broadcast(BroadcastAction.MARKING);
    }
}
