package mobi.myseries.application.schedule;

import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Specification;

public class Unaired extends TimelineMode {

    public Unaired(
            ScheduleSpecification specification,
            SeriesFollowingService following,
            UpdateService update,
            MarkingService marking) {
        super(specification, following, update, marking);
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
