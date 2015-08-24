package mobi.myseries.application.schedule;

import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Objects;

public class ToWatch extends ScheduleMode {

    public ToWatch(
            ScheduleSpecification specification,
            SeriesFollowingService following,
            UpdateService update,
            MarkingService marking) {
        super(specification, following, update, marking);
    }

    @Override
    protected void loadEpisodes() {
        for (Series s : mFollowing.getAllFollowedSeries()) {
            Episode nextToWatch = s.nextEpisodeToWatch(mSpecification.isSatisfiedBySpecialEpisodes());

            if (mSpecification.isSatisfiedBy(nextToWatch)) {
                mEpisodes.add(nextToWatch);
            }
        }
    }

    @Override
    protected MarkingListener markingListener() {
        return new MarkingListener() {
            @Override
            public void onMarked(Episode e) {
                onSuspectNextToWatchChanged(mFollowing.getFollowedSeries(e.seriesId()));
            }

            @Override
            public void onMarked(Season s) {
                onSuspectNextToWatchChanged(mFollowing.getFollowedSeries(s.seriesId()));
            }

            @Override
            public void onMarked(Series s) {
                onSuspectNextToWatchChanged(s);
            }

            private void onSuspectNextToWatchChanged(Series s) {
                if (!mSpecification.isSatisfiedByEpisodesOfSeries(s.id())) { return; }

                Episode currentToWatch = episodeOf(s.id());
                Episode nextToWatch = s.nextEpisodeToWatch(mSpecification.isSatisfiedBySpecialEpisodes());

                if (!Objects.areDifferent(currentToWatch, nextToWatch)) { return; }

                boolean removed = currentToWatch != null && mEpisodes.remove(currentToWatch);
                boolean added = nextToWatch != null && mEpisodes.add(nextToWatch);

                if (removed || added) {
                    sortEpisodes();
                    notifyOnScheduleStateChanged();
                }
            }

            private Episode episodeOf(int seriesId) {
                for (Episode e : mEpisodes) {
                    if (e.seriesId() == seriesId) {
                        return e;
                    }
                }

                return null;
            }
        };
    }
}
