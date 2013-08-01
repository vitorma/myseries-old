package mobi.myseries.application.schedule;

import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Specification;

public class ToWatch extends ScheduleMode implements SeriesListener {

    public ToWatch(ScheduleSpecification specification, SeriesRepository repository, FollowSeriesService following, UpdateService update) {
        super(specification, repository, following, update);
    }

    @Override
    protected void loadEpisodes() {
        for (Series s : this.repository.getAll()) {
            Episode nextToSee = s.nextEpisodeToSee(this.specification.isSatisfiedBySpecialEpisodes());

            if (this.specification.isSatisfiedBy(nextToSee)) {
                this.episodes.add(nextToSee);
            }

            s.register(this);
        }
    }

    /* SeriesListener */

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        Episode oldNextToSee = this.includedEpisodeOf(series);

        if (oldNextToSee != null) {
            this.episodes.remove(oldNextToSee);
        }

        Episode newNextToSee = series.nextEpisodeToSee(this.specification.isSatisfiedBySpecialEpisodes());

        if ((newNextToSee != null) && this.specification.isSatisfiedBy(newNextToSee)) {
            this.episodes.add(newNextToSee);
        }

        this.sortEpisodes();
        this.notifyOnScheduleStateChanged();
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {}

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {}

    /* Auxiliary */

    private Episode includedEpisodeOf(Series series) {
        Specification<Episode> specification = ToWatch.seriesIdSpecification(series.id());

        for (Episode e : this.episodes) {
            if (specification.isSatisfiedBy(e)) {
                return e;
            }
        }

        return null;
    }

    private static Specification<Episode> seriesIdSpecification(final int seriesId) {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.seriesId() == seriesId;
            }
        };
    }

    @Override
    public void onMarkAsSeen(Series series) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMarkAsNotSeen(Series series) {
        // TODO Auto-generated method stub

    }
}