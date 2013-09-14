package mobi.myseries.application.schedule;

import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Specification;

public abstract class TimelineMode extends ScheduleMode {

    public TimelineMode(
            ScheduleSpecification specification,
            SeriesFollowingService following,
            UpdateService update,
            MarkingService marking) {
        super(specification, following, update, marking);
    }

    @Override
    protected void loadEpisodes() {
        for (Series s : mFollowing.getAllFollowedSeries()) {
            mEpisodes.addAll(s.episodesBy(timelineSpecification()));
        }
    }

    private Specification<Episode> timelineSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.airDate() != null &&
                        airDateSpecification().isSatisfiedBy(e) &&
                        mSpecification.isSatisfiedBy(e);
            }
        };
    }

    protected abstract Specification<Episode> airDateSpecification();

    @Override
    protected MarkingListener markingListener() {
        return new MarkingListener() {

            @Override
            public void onMarked(Episode e) {
                if (!mSpecification.isSatisfiedByEpisodesOfSeries(e.seriesId())) { return; }

                boolean added = false;
                boolean removed = false;

                if (timelineSpecification().isSatisfiedBy(e)) {
                    added = !mEpisodes.contains(e) && mEpisodes.add(e);
                } else {
                    removed = mEpisodes.remove(e);
                }

                if (added) { sortEpisodes(); }

                if (added || removed) { notifyOnScheduleStateChanged(); }
            }

            @Override
            public void onMarked(Season s) {
                if (!mSpecification.isSatisfiedByEpisodesOfSeries(s.seriesId())) { return; }

                boolean added = false;
                boolean removed = false;

                for (Episode e : s.episodes()) {
                    if (timelineSpecification().isSatisfiedBy(e)) {
                        added = added | (!mEpisodes.contains(e) && mEpisodes.add(e));
                    } else {
                        removed = removed | mEpisodes.remove(e);
                    }
                }

                if (added) { sortEpisodes(); }

                if (added || removed) { notifyOnScheduleStateChanged(); }
            }

            @Override
            public void onMarked(Series s) {
                if (!mSpecification.isSatisfiedByEpisodesOfSeries(s.id())) { return; }

                boolean added = false;
                boolean removed = false;

                for (Episode e : s.episodes()) {
                    if (timelineSpecification().isSatisfiedBy(e)) {
                        added = added | (!mEpisodes.contains(e) && mEpisodes.add(e));
                    } else {
                        removed = removed | mEpisodes.remove(e);
                    }
                }

                if (added) { sortEpisodes(); }

                if (added || removed) { notifyOnScheduleStateChanged(); }
            }
        };
    }
}
