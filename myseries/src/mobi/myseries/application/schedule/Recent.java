package mobi.myseries.application.schedule;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Specification;

public class Recent extends TimelineMode {

    public Recent(ScheduleSpecification specification, SeriesRepository repository, FollowSeriesService following, UpdateService update) {
        super(specification, repository, following, update);
    }

    @Override
    protected Specification<Episode> airDateSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return !e.airDate().after(Dates.now());
            }
        };
    }
}
