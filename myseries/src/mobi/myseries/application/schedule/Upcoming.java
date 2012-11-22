package mobi.myseries.application.schedule;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Specification;
import mobi.myseries.update.UpdateService;

public class Upcoming extends TimelineMode {

    public Upcoming(ScheduleSpecification parameters, SeriesRepository repository, FollowSeriesService following, UpdateService update) {
        super(parameters, repository, following, update);
    }

    @Override
    protected Specification<Episode> airDateSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.airDate().after(DatesAndTimes.now());
            }
        };
    }
}
