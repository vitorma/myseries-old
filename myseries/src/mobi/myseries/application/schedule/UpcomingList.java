package mobi.myseries.application.schedule;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Specification;

public class UpcomingList extends ScheduleList implements EpisodeListener {

    private UpcomingList(ScheduleParameters parameters, FollowSeriesService following, SeriesRepository repository) {
        super(parameters, following);

        for (Series s : repository.getAll()) {
            for (Episode e : s.episodes()) {
                if (upcomingSpecification(parameters).isSatisfiedBy(e)) {
                    this.add(e);
                    e.register(this);
                }
            }
        }
    }

    private static Specification<Episode> upcomingSpecification(final ScheduleParameters parameters) {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return (parameters.includesSpecialEpisodes() || e.isNotSpecial()) &&
                       (parameters.includesSeenEpisodes() || e.wasNotSeen()) &&
                       (e.getDate() != null && e.getDate().after(Dates.now()));
            }
        };
    }

    @Override
    public void onMarkAsSeen(Episode episode) {
        if (upcomingSpecification(this.parameters()).isSatisfiedBy(episode)) {return;}

        this.remove(episode);
        episode.deregister(this);
        this.notifyListeners();
    }

    @Override
    public void onMarkAsNotSeen(Episode episode) {
        if (upcomingSpecification(this.parameters()).not().isSatisfiedBy(episode)) {return;}

        this.add(episode);
        episode.register(this);
        this.notifyListeners();
    }

    @Override
    public void onMarkAsSeenBySeason(Episode episode) {
        if (upcomingSpecification(this.parameters()).isSatisfiedBy(episode)) {return;}

        this.remove(episode);
        episode.deregister(this);
        this.notifyListeners();
    }

    @Override
    public void onMarkAsNotSeenBySeason(Episode episode) {
        if (upcomingSpecification(this.parameters()).not().isSatisfiedBy(episode)) {return;}

        this.add(episode);
        episode.register(this);
        this.notifyListeners();
    }

    @Override
    public void onMerge(Episode episode) {}

    //SeriesFollowingListener-------------------------------------------------------------------------------------------

    @Override
    public void onFollowing(Series s) {
        for (Episode e : s.episodes()) {
            if (upcomingSpecification(this.parameters()).isSatisfiedBy(e)) {
                this.add(e);
                e.register(this);
            }
        }

        this.notifyListeners();
    }

    @Override
    public void onStopFollowing(Series s) {
        for (Episode e : s.episodes()) {
            if (this.remove(e)) {
                e.deregister(this);
            }
        }

        this.notifyListeners();
    }

    //Builder-----------------------------------------------------------------------------------------------------------

    public static class Builder extends ScheduleList.Builder {
        public Builder(SeriesRepository repository, FollowSeriesService following) {
            super(repository, following);
        }

        public ScheduleList build() {
            return new UpcomingList(this.parameters, this.following, this.repository);
        }
    }
}
