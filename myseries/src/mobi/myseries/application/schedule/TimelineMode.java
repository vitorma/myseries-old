package mobi.myseries.application.schedule;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Specification;

public abstract class TimelineMode extends ScheduleMode implements EpisodeListener {

    public TimelineMode(ScheduleSpecification specification, SeriesRepository repository, FollowSeriesService following, UpdateService update) {
        super(specification, repository, following, update);
    }

    @Override
    protected void loadEpisodes() {
        for (Series s : this.repository.getAll()) {
            for (Episode e : s.episodes()) {
                if (this.episodeSpecification().isSatisfiedBy(e)) {
                    e.register(this);
                    this.episodes.add(e);
                }
            }
        }
    }

    /* EpisodeListener */

    @Override
    public void onMarkAsSeen(Episode episode) {
        if (!this.specification.isSatisfiedBy(episode)) {
            this.episodes.remove(episode);
        }

        if (this.specification.isSatisfiedBy(episode) && !this.episodes.contains(episode)) {
            this.episodes.add(episode);
        }

        this.notifyOnScheduleStateChanged();
    }

    @Override
    public void onMarkAsNotSeen(Episode episode) {
        this.onMarkAsSeen(episode);
    }

    @Override
    public void onMarkAsSeenBySeason(Episode episode) {
        this.onMarkAsSeen(episode);
    }

    @Override
    public void onMarkAsNotSeenBySeason(Episode episode) {
        this.onMarkAsSeen(episode);
    }

    /* Auxiliary */

    private Specification<Episode> episodeSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.airDate() != null &&
                        TimelineMode.this.airDateSpecification().isSatisfiedBy(e) &&
                        TimelineMode.this.specification.isSatisfiedBy(e);
            }
        };
    }

    protected abstract Specification<Episode> airDateSpecification();
}
