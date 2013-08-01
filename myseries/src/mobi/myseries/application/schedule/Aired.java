package mobi.myseries.application.schedule;

import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Specification;

public class Aired extends TimelineMode {

    public Aired(ScheduleSpecification specification, SeriesRepository repository, FollowSeriesService following, UpdateService update) {
        super(specification, repository, following, update);
    }

    @Override
    protected Specification<Episode> airDateSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return !e.airDate().after(DatesAndTimes.now());
            }
        };
    }
}